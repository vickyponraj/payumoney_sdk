import 'package:flutter/material.dart';
import 'package:payumoney_sdk/payumoney_sdk.dart';
import 'package:payumoney_sdk_example/debit_card_widget.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final payu = PayumoneySdk();
  final TextEditingController cardController = TextEditingController();

  @override
  void initState() {
    super.initState();
    getPgModes();
  }

  void getPgModes() async {
    payu.getPgModes().then((value) {
      debugPrint(value.toString());
    }).onError((error, stackTrace) {
      debugPrint(error.toString());
      debugPrintStack(stackTrace: stackTrace);
    });
  }

  void startPayment() async {
    final data = await payu.buildAndPayUsingCoreSDK(
      amount: 501.toString(),
      transactionId: "M715621631622830383",
      phone: "8248611901",
      productInfo: "MutualFunds",
      firstName: "Vignesh",
      email: "tovickyoff@gmail.com",
      hash:
          "c9fc44afdb2ce455e89a77a1ce119168a8e3ecb9f58a08e214c724ff340095a86b026d540294562a105383220ba9127522874ad7da50f79fc8dae3739d0e64ab",
      isProduction: true,
      userCredentials: "Zhgh53 : tovickyoff@gmail.com",
      merchantKey: "3TnMpV",
      salt: "aGXzo7lo",
      merchantName: "Payu",
      accountNumber: "50100200350998",
      bankCode: "HDFNBTPV",
      showExitConfirmation: true,
      vasSDKHash:
          "b30bf9ab94528c7fb02717993f31c84621595f4fb90766654c1a08020de4860c9b0720da5ab6cdabbcdc0fe4068ef8821c2c03f7cfa14095cea7356c356bf886",
      mobileSDKHash:
          "857772bb2089b4f9b3c3b5c05eabfc8316155e18f67ca05110f8c8848f7cc10d84b37e450689354ddb75a55b5a80f35d1c068bfaa66006f467a12a708f166c6e",
      ifscCode: "HDFC0001867",
      paymentHash:
          "c9fc44afdb2ce455e89a77a1ce119168a8e3ecb9f58a08e214c724ff340095a86b026d540294562a105383220ba9127522874ad7da50f79fc8dae3739d0e64ab",
      failureURL:
          "https://aws-south.siply.in/siplyadminpayments/api/v1/admin/payments/callback?status=failed",
      successURL:
          "https://aws-south.siply.in/siplyadminpayments/api/v1/admin/payments/callback?status=success",
    );

    // Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Payumoney SDK'),
        ),
        body: Center(
          child: Column(
            children: [
              SizedBox(
                height: 100,
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: DebitCardWidget(
                  payuMoneySdk: this.payu,
                  controller: this.cardController,
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: TextField(
                  controller: cardController,
                  decoration: const InputDecoration(
                      border: OutlineInputBorder(),
                      hintText: 'Enter a search term'),
                ),
              ),
              TextButton(
                  onPressed: () {
                    startPayment();
                  },
                  child: Text("Tap me"))
            ],
          ),
        ),
      ),
    );
  }
}
