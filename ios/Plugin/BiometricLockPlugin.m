#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(BiometricLock, "BiometricLock",
           CAP_PLUGIN_METHOD(configure, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getConfiguration, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getBiometricMethod, CAPPluginReturnPromise);
)
