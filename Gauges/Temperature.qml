import QtQuick 2.0
import QtQuick.Extras 1.4
import QtQuick.Controls.Styles 1.4

Item {
    BaseGauge {
        anchors.top: parent.top
        anchors.bottom: parent.bottom
        anchors.left: parent.left
        anchors.right: parent.horizontalCenter

        anchors.topMargin: parent.height/10
        anchors.bottomMargin: parent.height/10
        anchors.rightMargin: parent.width/20
        anchors.leftMargin: parent.width/10

        maximumValue: 170
        style: BaseGaugeStyle {
            labelStepSize: 20
            minorTickmarkCount: 1
        }
        unit:"IGBT1(°C)"
        value: master.igbt1Temperature
    }
    BaseGauge {
        anchors.top: parent.top
        anchors.bottom: parent.bottom
        anchors.left: parent.horizontalCenter
        anchors.right: parent.right

        anchors.topMargin: parent.height/10
        anchors.bottomMargin: parent.height/10
        anchors.rightMargin: parent.width/20
        anchors.leftMargin: parent.width/10

        maximumValue: 170
        style: BaseGaugeStyle {
            labelStepSize: 20
            minorTickmarkCount: 1
        }
        unit:"IGBT2(°C)"
        value: master.igbt2Temperature
    }
}
