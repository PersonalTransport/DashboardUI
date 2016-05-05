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
            id: left
            anchors.top: parent.top
            anchors.bottom: parent.bottom
        }
        Lighting {
            anchors.top: parent.top
            anchors.bottom: parent.bottom
            anchors.left: left.right
            anchors.topMargin: parent.height / 5
            anchors.bottomMargin: parent.height / 5
        }

    }

}
