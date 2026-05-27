import SwiftUI

@main
struct GdxTeaVMIOSSpikeApp: App {
    var body: some Scene {
        WindowGroup {
            TeaVMHostView()
                .ignoresSafeArea()
        }
    }
}

struct TeaVMHostView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> TeaVMViewController {
        TeaVMViewController()
    }

    func updateUIViewController(_ uiViewController: TeaVMViewController, context: Context) {
    }
}
