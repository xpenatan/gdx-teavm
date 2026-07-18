import GameController

private final class TeaVMControllerRegistry {
    static let shared = TeaVMControllerRegistry()

    private var nextHandle: Int64 = 1
    private var handleByController: [ObjectIdentifier: Int64] = [:]
    private var controllerByHandle: [Int64: GCController] = [:]

    func connectedControllers() -> [GCController] {
        GCController.controllers()
    }

    func handle(at index: Int32) -> Int64 {
        let controllers = connectedControllers()
        let arrayIndex = Int(index)
        guard arrayIndex >= 0 && arrayIndex < controllers.count else {
            return 0
        }

        let controller = controllers[arrayIndex]
        let identifier = ObjectIdentifier(controller)
        if let handle = handleByController[identifier] {
            return handle
        }

        let handle = nextHandle
        nextHandle += 1
        handleByController[identifier] = handle
        controllerByHandle[handle] = controller
        return handle
    }

    func controller(for handle: Int64) -> GCController? {
        controllerByHandle[handle]
    }

    func isConnected(_ handle: Int64) -> Bool {
        guard let controller = controllerByHandle[handle] else {
            return false
        }
        return connectedControllers().contains { $0 === controller }
    }

    func release(_ handle: Int64) {
        guard let controller = controllerByHandle.removeValue(forKey: handle) else {
            return
        }
        handleByController.removeValue(forKey: ObjectIdentifier(controller))
    }
}

@_cdecl("gdx_teavm_ios_controller_count")
func gdxTeaVMIOSControllerCount() -> Int32 {
    Int32(TeaVMControllerRegistry.shared.connectedControllers().count)
}

@_cdecl("gdx_teavm_ios_controller_handle_at")
func gdxTeaVMIOSControllerHandleAt(_ index: Int32) -> Int64 {
    TeaVMControllerRegistry.shared.handle(at: index)
}

@_cdecl("gdx_teavm_ios_controller_release")
func gdxTeaVMIOSControllerRelease(_ handle: Int64) {
    TeaVMControllerRegistry.shared.release(handle)
}

@_cdecl("gdx_teavm_ios_controller_connected")
func gdxTeaVMIOSControllerConnected(_ handle: Int64) -> Int32 {
    TeaVMControllerRegistry.shared.isConnected(handle) ? 1 : 0
}

@_cdecl("gdx_teavm_ios_controller_button")
func gdxTeaVMIOSControllerButton(_ handle: Int64, _ buttonCode: Int32) -> Int32 {
    guard let controller = TeaVMControllerRegistry.shared.controller(for: handle) else {
        return 0
    }

    let pressed: Bool
    if let gamepad = controller.extendedGamepad {
        switch buttonCode {
        case 0: pressed = gamepad.buttonA.isPressed
        case 1: pressed = gamepad.buttonB.isPressed
        case 2: pressed = gamepad.buttonX.isPressed
        case 3: pressed = gamepad.buttonY.isPressed
        case 4: pressed = gamepad.leftShoulder.isPressed
        case 5: pressed = gamepad.rightShoulder.isPressed
        case 6: pressed = gamepad.buttonOptions?.isPressed ?? false
        case 7: pressed = gamepad.buttonMenu.isPressed
        case 8: pressed = gamepad.leftTrigger.isPressed
        case 9: pressed = gamepad.rightTrigger.isPressed
        case 10: pressed = gamepad.leftThumbstickButton?.isPressed ?? false
        case 11: pressed = gamepad.rightThumbstickButton?.isPressed ?? false
        case 12: pressed = gamepad.dpad.up.isPressed
        case 13: pressed = gamepad.dpad.down.isPressed
        case 14: pressed = gamepad.dpad.left.isPressed
        case 15: pressed = gamepad.dpad.right.isPressed
        default: pressed = false
        }
    }
    else if let gamepad = controller.microGamepad {
        switch buttonCode {
        case 0: pressed = gamepad.buttonA.isPressed
        case 2: pressed = gamepad.buttonX.isPressed
        case 12: pressed = gamepad.dpad.up.isPressed
        case 13: pressed = gamepad.dpad.down.isPressed
        case 14: pressed = gamepad.dpad.left.isPressed
        case 15: pressed = gamepad.dpad.right.isPressed
        default: pressed = false
        }
    }
    else {
        pressed = false
    }
    return pressed ? 1 : 0
}

@_cdecl("gdx_teavm_ios_controller_axis")
func gdxTeaVMIOSControllerAxis(_ handle: Int64, _ axisCode: Int32) -> Float {
    guard let controller = TeaVMControllerRegistry.shared.controller(for: handle) else {
        return 0
    }

    if let gamepad = controller.extendedGamepad {
        switch axisCode {
        case 0: return gamepad.leftThumbstick.xAxis.value
        case 1: return -gamepad.leftThumbstick.yAxis.value
        case 2: return gamepad.rightThumbstick.xAxis.value
        case 3: return -gamepad.rightThumbstick.yAxis.value
        case 4: return gamepad.leftTrigger.value
        case 5: return gamepad.rightTrigger.value
        default: return 0
        }
    }
    if let gamepad = controller.microGamepad {
        switch axisCode {
        case 0: return gamepad.dpad.xAxis.value
        case 1: return -gamepad.dpad.yAxis.value
        default: return 0
        }
    }
    return 0
}
