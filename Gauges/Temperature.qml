import QtQuick 2.0
import QtQuick.Extras 1.4
import QtQuick.Controls.Styles 1.4

BaseGauge {
    maximumValue: 220
    style: BaseGaugeStyle {
        labelStepSize: 20
        minorTickmarkCount: 1
    }
    unit:"IGBT(°F)"
}
