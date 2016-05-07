import QtQuick 2.0
import QtQuick.Extras 1.4

Item {
    Item {
        id: item1
        anchors.top: parent.top
        anchors.left: parent.left
        anchors.right: parent.right
        anchors.bottom: parent.bottom
        anchors.leftMargin: parent.width/10
        anchors.rightMargin: parent.width/10
        anchors.topMargin: parent.height/20
        anchors.bottomMargin: parent.height/10

        /*Battery {
            id: batteryGauge
            width: parent.width*0.20
            height: width
            anchors.left: throttleGauge.right
            anchors.right: speedometerGauge.left
            anchors.bottom: parent.verticalCenter
            value: master.batteryLife
        }

        Temperature {
            id: temperatureGauge
            width: parent.width*0.20
            height: width
            anchors.left: throttleGauge.right
            anchors.top: batteryGauge.bottom
            anchors.right: batteryGauge.right
            value: master.igbtTemperature
        }

        Current {
            id: currentGauge
            anchors.top: batteryGauge.top
            anchors.left: speedometerGauge.right
            anchors.bottom: batteryGauge.bottom
            anchors.right: parent.right
            value: master.usageCurrent
        }

        Voltage {
            id: voltageGauge
            anchors.top: currentGauge.bottom
            anchors.bottom: temperatureGauge.bottom
            anchors.right: currentGauge.right
            anchors.left: currentGauge.left
            value: master.batteryVoltage
        }*/

        Speedometer {
            id: speedometerGauge
            width: parent.width*.60
            anchors.horizontalCenter: parent.horizontalCenter
            anchors.bottom: parent.bottom
            anchors.top: parent.top
            value: master.speed
        }

        TurnSignal {
            id: leftTurnSingal
            //anchors.bottom: batteryGauge.top
            anchors.horizontalCenter: speedometerGauge.horizontalCenter
            anchors.horizontalCenterOffset: -speedometerGauge.width/2
            active: master.signalLightState == 2 || master.signalLightState == 3
        }

        TurnSignal {
            id: rightTurnSignal
            anchors.horizontalCenterOffset: speedometerGauge.width/2
            anchors.horizontalCenter: speedometerGauge.horizontalCenter
           // anchors.bottom: currentGauge.top
            active: master.signalLightState == 1 || master.signalLightState == 3
        }

        Gauge {
            id: throttleGauge
            width: 30
            anchors.left: parent.left
            anchors.bottom: parent.bottom
            anchors.top: parent.top
            value: master.throttlePosition
        }
    }
}
