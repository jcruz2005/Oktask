import UIKit
import Capacitor

/**
 * OKtask - Gestor de tareas con Pomodoro
 * App para iOS
 */
@UIApplicationMain
class AppDelegate: CAPBridgeDelegate {
    
    override func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        return true
    }
    
    override func applicationWillResignActive(_ application: UIApplication) {
        // Guardar estado cuando la app pasa a background
    }
    
    override func applicationDidEnterBackground(_ application: UIApplication) {
        // Manejar tareas en background (Pomodoro)
    }
    
    override func applicationWillEnterForeground(_ application: UIApplication) {
        // Restaurar estado cuando la app vuelve a foreground
    }
    
    override func applicationDidBecomeActive(_ application: UIApplication) {
        // Actualizar UI
    }
}
