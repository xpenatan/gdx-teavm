import MetalANGLEKit
import UIKit

final class TeaVMViewController: UIViewController, MGLKViewDelegate {
    private let maxPointers = 20
    private let statusLabel = UILabel()
    private var glContext: MGLContext!
    private var glView: MGLKView {
        view as! MGLKView
    }
    private var displayLink: CADisplayLink?
    private var started = false
    private var touchPointers: [ObjectIdentifier: Int32] = [:]
    private var availablePointers: [Int32] = Array(0..<20).map(Int32.init)

    override func loadView() {
        let context = MGLContext(api: MGLRenderingAPI(rawValue: 2))
        glContext = context
        MGLContext.setCurrent(context)

        guard let glView = MGLKView(frame: UIScreen.main.bounds, context: context) else {
            fatalError("Unable to create ANGLE view")
        }
        glView.contentScaleFactor = UIScreen.main.scale
        glView.drawableColorFormat = MGLDrawableColorFormat(rawValue: 32)
        glView.drawableDepthFormat = MGLDrawableDepthFormat(rawValue: 16)
        glView.drawableStencilFormat = MGLDrawableStencilFormat(rawValue: 8)
        glView.drawableMultisample = MGLDrawableMultisample(rawValue: 0)
        glView.enableSetNeedsDisplay = false
        glView.delegate = self
        updateDrawableLayer(for: glView)
        view = glView
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black
        view.isMultipleTouchEnabled = true
        setupStatusLabel()
        startTeaVM()
        resizeTeaVM()

        let displayLink = CADisplayLink(target: self, selector: #selector(renderFrame))
        displayLink.add(to: .main, forMode: .common)
        self.displayLink = displayLink
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateDrawableLayer(for: glView)
        resizeTeaVM()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        gdx_teavm_ios_resume()
        displayLink?.isPaused = false
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        displayLink?.isPaused = true
        gdx_teavm_ios_pause()
    }

    deinit {
        displayLink?.invalidate()
        gdx_teavm_ios_dispose()
        if MGLContext.current() === glContext {
            MGLContext.setCurrent(nil)
        }
    }

    override var prefersStatusBarHidden: Bool {
        true
    }

    private func startTeaVM() {
        guard !started else {
            return
        }
        started = true
        statusLabel.text = "Starting TeaVM..."
        let workingDirectory = Bundle.main.resourcePath.map { "\($0)/assets" }
            ?? FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first?.path
            ?? NSHomeDirectory()
        workingDirectory.withCString { path in
            _ = gdx_teavm_ios_start(path)
        }
        statusLabel.text = "TeaVM running"
    }

    private func setupStatusLabel() {
        statusLabel.translatesAutoresizingMaskIntoConstraints = false
        statusLabel.text = "TeaVM loading"
        statusLabel.textColor = .white
        statusLabel.font = UIFont.monospacedSystemFont(ofSize: 18, weight: .semibold)
        statusLabel.textAlignment = .center
        statusLabel.numberOfLines = 0
        view.addSubview(statusLabel)
        NSLayoutConstraint.activate([
            statusLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            statusLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            statusLabel.leadingAnchor.constraint(greaterThanOrEqualTo: view.leadingAnchor, constant: 24),
            statusLabel.trailingAnchor.constraint(lessThanOrEqualTo: view.trailingAnchor, constant: -24)
        ])
    }

    private func resizeTeaVM() {
        let scale = glView.window?.screen.scale ?? UIScreen.main.scale
        let pixelWidth = Int32(max(1, (glView.bounds.width * scale).rounded()))
        let pixelHeight = Int32(max(1, (glView.bounds.height * scale).rounded()))
        gdx_teavm_ios_resize(pixelWidth, pixelHeight, Float(scale))
    }

    private func updateDrawableLayer(for view: MGLKView) {
        let scale = view.window?.screen.scale ?? UIScreen.main.scale
        view.contentScaleFactor = scale
        view.layer.contentsScale = scale
        view.glLayer.contentsScale = scale
        view.glLayer.frame = view.bounds
    }

    private func drawablePixelSize(for view: MGLKView) -> (width: Int32, height: Int32) {
        let scale = view.window?.screen.scale ?? UIScreen.main.scale
        let width = Int32(max(1, (view.bounds.width * scale).rounded()))
        let height = Int32(max(1, (view.bounds.height * scale).rounded()))
        return (width, height)
    }

    @objc private func renderFrame() {
        glView.display()
    }

    func mglkView(_ view: MGLKView, drawIn rect: CGRect) {
        MGLContext.setCurrent(glContext, for: view.glLayer)
        updateDrawableLayer(for: view)
        view.bindDrawable()
        resizeTeaVM()
        let pixelSize = drawablePixelSize(for: view)
        gdx_teavm_ios_set_viewport(pixelSize.width, pixelSize.height)
        gdx_teavm_ios_render()
        updateStatusLabel()
    }

    private func updateStatusLabel() {
        switch gdx_teavm_ios_status_code() {
        case let code where code < 0:
            statusLabel.isHidden = false
            statusLabel.text = "TeaVM error \(abs(code))"
        case 1:
            statusLabel.isHidden = false
            statusLabel.text = "TeaVM created"
        case 2:
            statusLabel.isHidden = true
        default:
            break
        }
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        sendTouches(touches, type: 0, releasePointers: false)
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        sendTouches(touches, type: 2, releasePointers: false)
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        sendTouches(touches, type: 1, releasePointers: true)
    }

    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        sendTouches(touches, type: 3, releasePointers: true)
    }

    private func sendTouches(_ touches: Set<UITouch>, type: Int32, releasePointers: Bool) {
        let scale = view.window?.screen.scale ?? UIScreen.main.scale
        for touch in touches {
            let pointer = pointerIndex(for: touch)
            let location = touch.location(in: view)
            let pressure = normalizedPressure(for: touch)
            gdx_teavm_ios_touch(
                type,
                pointer,
                Int32((location.x * scale).rounded()),
                Int32((location.y * scale).rounded()),
                pressure
            )
            if releasePointers {
                releasePointer(for: touch)
            }
        }
    }

    private func pointerIndex(for touch: UITouch) -> Int32 {
        let id = ObjectIdentifier(touch)
        if let existing = touchPointers[id] {
            return existing
        }
        let pointer = availablePointers.isEmpty ? Int32(touchPointers.count % maxPointers) : availablePointers.removeFirst()
        touchPointers[id] = pointer
        return pointer
    }

    private func releasePointer(for touch: UITouch) {
        let id = ObjectIdentifier(touch)
        guard let pointer = touchPointers.removeValue(forKey: id) else {
            return
        }
        availablePointers.append(pointer)
        availablePointers.sort()
    }

    private func normalizedPressure(for touch: UITouch) -> Float {
        if touch.maximumPossibleForce > 0 {
            return Float(touch.force / touch.maximumPossibleForce)
        }
        return 1
    }
}
