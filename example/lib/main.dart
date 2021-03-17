import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:payumoney_sdk/payumoney_sdk.dart';
import 'package:payumoney_sdk_example/models/payu_response_model.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool isPayuSetup=false;
  final payu=PayumoneySdk();

  @override
  void initState() {
    super.initState();
    initPayu();
  }


  Future<void> initPayu() async {
    bool _ispayuSetup;


    //phone:"8248611901"
    //productName:"Gold"
    //firstName:"Shubho"
    // email: "TestEmail@gmail.com"
    //Merchant key:QylhKRVd
    //MID:5960507
    //Salt:seVTUgzrgE

    try {
     _ispayuSetup=true;
    ///New ids
     // await payu.setupPayuDetails(
     //     merchantID: "4957536",
     //     merchantKey: "mWI8Vl",
     //     isProduction: false,
     //     activityTitle: "Siply Test",
     //     colorCode: "0xFF82B1FF",
     //     disableExitConfirmation: false);



      // await payu.setupPayuDetails(
     //     merchantID: "5960507",
     //     merchantKey: "QylhKRVd",
     //     isProduction: false,
     //     activityTitle: "Siply Test",
     //     colorCode: "0xFF82B1FF",
     //     disableExitConfirmation: false);


    } on PlatformException {
      _ispayuSetup=false;
    }

    if (!mounted) return;



    setState(() {
      isPayuSetup =_ispayuSetup;
    });
  }



  void startPayment()async{


    final payuOrderId=await getPayuOrderId();

    if(isPayuSetup){


      // payu.startPayment(
      //     txnID: "G4761615806485885",//Add Txn Id
      //     amount: "400.00",
      //     name:"Shubho",
      //     email: "Test@gmail.com",
      //     phone:"8248611901",
      //     productName:"Gold",
      //     hash: payuOrderId.data.hash);

      payu.buildPaymentParams(
          amount: "400.0",
          transactionId: payuOrderId.data.orderId,
          phone: payuOrderId.data.mobile,
          productInfo:payuOrderId.data.productName,
          firstName: payuOrderId.data.name,
          email: payuOrderId.data.email,
          hash: payuOrderId.data.hash,
          isProduction: true,
          userCredentials: payuOrderId.data.mobile,
          merchantKey: "mWI8Vl");


      // payu.startPayment(
      //     txnID:payuOrderId.data.orderId,//Add Txn Id
      //     amount: "400.00",
      //     name:payuOrderId.data.name,
      //     email: payuOrderId.data.email,
      //     phone:payuOrderId.data.mobile,
      //     productName:payuOrderId.data.productName,
      //     hash: payuOrderId.data.hash);


    }


  }


  Future<PayuResponseModel> getPayuOrderId()async{
    final response= await http.post(
      "https://aws-east.siply.in/siplyadminuser/api/v2/admin/auth/investments/checkoutTransactions?vendor=cqmoneygold",
        headers: {
          "Authorization":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb3VudHJ5X2NvZGUiOiI5MSIsImVtYWlsIjoibXNtc0BoaC5jb20iLCJlbXBfaWQiOjcwNDUyMDI4NzUsImVtcHJfaWQiOiJTQSIsImV4cCI6IjE2MTcxMDAxNTg3NzYiLCJpZCI6NDc2LCJtb2JpbGVOdW1iZXIiOjcwNDUyMDI4NzUsIm5hbWUiOiJraXNob3IiLCJzb3VyY2UiOiJ3ZWIiLCJ1c2VyX2lkIjoiU0RhSXpacE1SIiwidXNlcm5hbWUiOjcwNDUyMDI4NzV9.-x8SvR42KBIRQHfZEDtyTch0DoMJAu5g1q7GiveN5Pc"
        },

        body: jsonEncode({
          "gold_list":[
            {
              "vendor_name" : "Augmont Goldtech Private Limited",
              "block_id": "DGoUN6sLV",
              "gold_amount": 400.00,
              "lock_price": 69.41
            }
          ]
        })

    );


    return PayuResponseModel.fromJson(jsonDecode(response.body));
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
              Text('Payu  setup on: $isPayuSetup\n'),
              SizedBox(height: 100,),
              FlatButton(onPressed: (){
                startPayment();
              }, child: Text("Tap me"))
            ],
          ),
        ),
      ),
    );
  }
}
