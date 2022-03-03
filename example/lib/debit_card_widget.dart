import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:payumoney_sdk/payumoney_sdk.dart';
import 'package:payumoney_sdk_example/assets/assets.dart';

class DebitCardWidget extends StatefulWidget {

  final TextEditingController controller;
  final PayumoneySdk payuMoneySdk;

  const DebitCardWidget({Key key, this.controller ,this.payuMoneySdk}) : super(key: key);

  @override
  _DebitCardWidgetState createState() => _DebitCardWidgetState();
}

class _DebitCardWidgetState extends State<DebitCardWidget> {

  String cardIssuer;
  Pattern pattern = r'^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\d{3})\d{11})$';

  @override
  void initState() {
    super.initState();

    widget.controller.addListener(onTextChange);

  }
  @override
  Widget build(BuildContext context) {
    return TextField(
      keyboardType: TextInputType.number,
      controller: widget.controller,
      decoration: InputDecoration(
        suffixIcon: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8.0),
          child: Image.asset(getAssetPath(),width: 36,),
        ),
          border: OutlineInputBorder(),
          hintText: 'Enter Card Details'),
    );
  }

  String getAssetPath(){
    if(cardIssuer==null){
      return Assets.diner;
    }
    switch (cardIssuer) {
      case "VISA":
        return Assets.logoVisa;
      case "LASER":
        return Assets.laser;
      case "DISCOVER":
        return Assets.discover;
      case "MAES":
        return Assets.masIcon;
      case "MAST":
        return Assets.mcIcon;
      case "AMEX":
        return Assets.aMex;
      case "DINR":
        return Assets.diner;
      case "JCB":
        return Assets.jcb;
      case "SMAE":
        return Assets.maestro;
      case "RUPAY":
        return Assets.ruPay;
      default: return Assets.ruPay;
    }
  }


  void onTextChange() async {

    RegExp regex = new RegExp(pattern);

    Map<dynamic, dynamic> validateCardNumber = await widget.payuMoneySdk.validateCardNumber(widget.controller.text);

    debugPrint(validateCardNumber.toString());

    var getCardIssuer = validateCardNumber["issuer"] as String;

    if(widget.controller.text.length > 5){
      if(null == cardIssuer){
        setState(() {
          debugPrint("Issuer : if$getCardIssuer");

          this.cardIssuer = getCardIssuer;
        });
      }
    }else{
      setState(() {
        debugPrint("Issuer : else $getCardIssuer");

        this.cardIssuer = null;
      });

    }






  }
}
