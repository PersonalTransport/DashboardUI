import QtQuick 2.0
import QtQuick.Extras 1.4
import QtQuick.Controls.Styles 1.4

BaseGauge {
    maximumValue: 100
    style: BaseGaugeStyle {
        labelStepSize: 10
        minorTickmarkCount: 4
    }
    unit: "Battery Life(%)"
    value: master.batteryLife
}
