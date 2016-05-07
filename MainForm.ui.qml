import QtQuick 2.4
import QtQuick.Window 2.0
import QtQuick.Controls 1.4
import QtQuick.Controls.Styles 1.4
import QtQuick.Layouts 1.1
import QtQuick.Extras 1.4

import "Gauges"
import "Lighting"

Rectangle {
    color: "black"
    id: rectangle1

    HorizontalPager {

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
        Lighting {
            anchors.top: parent.top
            anchors.bottom: parent.bottom
            anchors.left: temp.right
        }

    }

}
