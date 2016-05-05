import QtQuick 2.0
import QtQuick.Extras 1.4
import QtQuick.Controls.Styles 1.4

BaseGauge {
    maximumValue: 40
    style: BaseGaugeStyle {
        labelStepSize: 5
        minorTickmarkCount: 9
    }
    unit:"Speed(MPH)"
}
