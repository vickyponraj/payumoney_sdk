#import "PayumoneySdkPlugin.h"
#if __has_include(<payumoney_sdk/payumoney_sdk-Swift.h>)
#import <payumoney_sdk/payumoney_sdk-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "payumoney_sdk-Swift.h"
#endif

@implementation PayumoneySdkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPayumoneySdkPlugin registerWithRegistrar:registrar];
}
@end
