import QtQuick 2.4

import "Gauges"
import "Lighting"

import QtQuick.Extras 1.4
import QtQuick.Controls.Styles 1.4

Rectangle {
    color: "black"

    Current {
        id: current
        anchors.left: parent.left
        anchors.top: parent.top
        anchors.bottom: parent.bottom
        width: parent.width / 3
    }

    BaseGauge {
        id: temp1
        anchors.left: current.right
        anchors.right: parent.horizontalCenter
        anchors.top: parent.top

        maximumValue: 170
        style: BaseGaugeStyle {
            labelStepSize: 20
            minorTickmarkCount: 1
        }
        unit:"IGBT1(°C)"
        value: master.igbt1Temperature
    }

    BaseGauge {
        id: temp2
        anchors.right: speedometer.left
        anchors.left: parent.horizontalCenter
        anchors.top: parent.top

        maximumValue: 170
        style: BaseGaugeStyle {
            labelStepSize: 20
            minorTickmarkCount: 1
        }
        unit:"IGBT2(°C)"
        value: master.igbt2Temperature
    }

    Speedometer {
        id: speedometer
        anchors.right: parent.right
        anchors.top: parent.top
        anchors.bottom: parent.bottom
        width: parent.width / 3
    }

    Lighting {
        anchors.right: speedometer.left
        anchors.left: current.right
        anchors.top: temp1.bottom
        anchors.bottom: parent.bottom
    }

    /*HorizontalPager {
        anchors.fill: parent

        Gauges {
            id: gauges
            anchors.top: parent.top
            anchors.bottom: parent.bottom
        }

        Temperature {
            id: temp
            anchors.top: parent.top
            anchors.bottom: parent.bottom
            anchors.left: gauges.right
        }

        Current {
            id: current
            anchors.top: parent.top
            anchors.bottom: parent.bottom
            anchors.left: temp.right
        }

        Lighting {
            anchors.top: parent.top
            anchors.bottom: parent.bottom
            anchors.left: current.right
        }

    }*/
}
