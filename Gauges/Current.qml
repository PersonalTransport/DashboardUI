import QtQuick 2.0
import QtQuick.Extras 1.4
import QtQuick.Controls.Styles 1.4

BaseGauge {
    maximumValue: 300
    style: BaseGaugeStyle {
        labelStepSize: 25
        minorTickmarkCount: 0
    }
    unit: "Current Draw (A)"
    value: master.usageCurrent
}
