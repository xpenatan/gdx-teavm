import UIKit

final class TeaVMViewController: UIViewController {
    private let maxPointers = 20
    private var displayLink: CADisplayLink?
    private var started = false
    private var touchPointers: [ObjectIdentifier: Int32] = [:]
    private var availablePointers: [Int32] = Array(0..<20).map(Int32.init)

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black
        view.isMultipleTouchEnabled = true
        startTeaVM()
        resizeTeaVM()

        let displayLink = CADisplayLink(target: self, selector: #selector(renderFrame))
        displayLink.add(to: .main, forMode: .common)
        self.displayLink = displayLink
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
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
    }

    override var prefersStatusBarHidden: Bool {
        true
    }

    private func startTeaVM() {
        guard !started else {
            return
        }
        started = true
        let workingDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first?.path
            ?? NSHomeDirectory()
        workingDirectory.withCString { path in
            _ = gdx_teavm_ios_start(path)
        }
    }

    private func resizeTeaVM() {
        let scale = view.window?.screen.scale ?? UIScreen.main.scale
        let pixelWidth = Int32((view.bounds.width * scale).rounded())
        let pixelHeight = Int32((view.bounds.height * scale).rounded())
        gdx_teavm_ios_resize(pixelWidth, pixelHeight, Float(scale))
    }

    @objc private func renderFrame() {
        gdx_teavm_ios_render()
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
