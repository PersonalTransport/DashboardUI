import QtQuick 2.7
import QtQuick.Controls 2.0
import QtQuick.Layouts 1.0
import QtCharts 2.0

Rectangle {
    color: "darkgrey"
    SwipeView {
        anchors.fill: parent
        currentIndex: 0

        Rectangle {
            id: rectangle1
            color: "#000000"

            Text {
                id: text1
                x: 326
                y: 241
                color: "#ffffff"
                text: master.file
                anchors.horizontalCenter: parent.horizontalCenter
                anchors.verticalCenter: parent.verticalCenter
                font.pixelSize: 30
            }

        }


        GaguePage {
            //anchors.fill: parent
        }

        DataGraph {
            //anchors.fill: parent
        }


    }
}
