import QtQuick 2.0
import QtQuick.Extras 1.4
import QtQuick.Controls.Styles 1.4

CircularGauge {
    id: circularGauge1
    property string unit: ""

    Text {
        text: unit
        anchors.horizontalCenter: circularGauge1.horizontalCenter
        anchors.verticalCenter: circularGauge1.verticalCenter
        anchors.verticalCenterOffset: (circularGauge1.height < circularGauge1.width ? circularGauge1.height : circularGauge1.width)/2 - (circularGauge1.height < circularGauge1.width ? circularGauge1.height : circularGauge1.width)/3
        font.pixelSize: anchors.verticalCenterOffset / 2
        color: "white"
        font.family: "Ubuntu Mono"
    }
}
