package io.kazutoiris.pure.shot;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedModule implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("android")) {
            return;
        }

        Class<?> builderClass = XposedHelpers.findClassIfExists(
            "android.view.SurfaceControl$Builder",
            lpparam.classLoader
        );

        if (builderClass == null) {
            return;
        }

        XposedBridge.hookAllMethods(builderClass, "build", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String name = (String) XposedHelpers.getObjectField(param.thisObject, "mName");
                if (name != null) {
                    String lower = name.toLowerCase();
                    // Filters Status Bar, Navigation Bar, 3-button bar, and Gesture pill/handle
                    if (lower.contains("statusbar")
                            || lower.contains("navigationbar")
                            || lower.contains("navbar")
                            || lower.contains("navigation")
                            || lower.contains("gesturehandle")
                            || lower.contains("gestural")) {

                        int flags = XposedHelpers.getIntField(param.thisObject, "mFlags");
                        // Apply SKIP_SCREENSHOT (0x00000040)
                        XposedHelpers.setIntField(param.thisObject, "mFlags", flags | 0x40);
                    }
                }
            }
        });
    }
}
