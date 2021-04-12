import 'package:flutter/material.dart';

import 'package:payumoney_sdk/payumoney_sdk.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final payu = PayumoneySdk();

  @override
  void initState() {
    super.initState();
    startPayment();
  }

  void startPayment() async {
    final data = await payu.buildPaymentParams(
      amount: "400.0",
      transactionId: "ORDER_123",
      phone: "9999999999",
      productInfo: "Nike shoes",
      firstName: "First name",
      email: "snooze@payu.in",
      hash: "My hash will be here",
      isProduction: false,
      userCredentials: "9999999999",
      merchantKey: "gtKFFX",
      salt: "eCwWELxi",
      merchantName: "My merchant name"
    );

    Navigator.pop(context);
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
              FlatButton(
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
