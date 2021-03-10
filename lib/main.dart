import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MethodChannelImplementation(),
    );
  }
}

class MethodChannelImplementation extends StatefulWidget {
  @override
  _MethodChannelImplementationState createState() =>
      _MethodChannelImplementationState();
}

class _MethodChannelImplementationState
    extends State<MethodChannelImplementation> {
  String batteryValue;
  static const batteryChannel = MethodChannel("battery");
  static const dailyLimit = MethodChannel("dailyLimit");

  @override
  void initState() {
    super.initState();
  }

  void dailyUsage() async {
    try {
      var result = await dailyLimit.invokeMethod('getDailyUsage');
    } on PlatformException catch (e) {}
  }

  void triggerChannel() async {
    String batteryPercantage;
    try {
      var result = await batteryChannel.invokeMethod('getBatteryLevel');
      batteryPercantage = 'Battery level at $result';
    } on PlatformException catch (e) {
      batteryPercantage = "Failed to get battery level ${e.message}";
    }
    setState(() {
      batteryValue = batteryPercantage;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Method Channel"),
      ),
      body: Center(
        child: Column(
          children: [
            Text(batteryValue.toString()),
            ElevatedButton(
              onPressed: () {
                triggerChannel();
              },
              child: Text("Trigger Channel"),
            )
          ],
        ),
      ),
    );
  }
}
