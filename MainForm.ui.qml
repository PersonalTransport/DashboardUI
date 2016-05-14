import QtQuick 2.4

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

    }
}
