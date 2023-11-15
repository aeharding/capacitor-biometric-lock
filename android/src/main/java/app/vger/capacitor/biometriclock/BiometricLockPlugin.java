package app.vger.capacitor.biometriclock;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "BiometricLock")
public class BiometricLockPlugin extends Plugin {

    @PluginMethod
    public void configure(PluginCall call) {
        call.unimplemented();
    }

    @PluginMethod
    public void getConfiguration(PluginCall call) {
        call.unimplemented();
    }

    @PluginMethod
    public void getBiometricMethod(PluginCall call) {
        JSObject result = new JSObject();
        result.put("biometricMethod", 0);

        call.resolve(result);
    }
}
