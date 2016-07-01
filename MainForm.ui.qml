import QtQuick 2.7
import QtQuick.Controls 2.0
import QtQuick.Layouts 1.0
import QtCharts 2.0

Rectangle {
    color: "darkgrey"
    SwipeView {
        anchors.fill: parent
        currentIndex: 0

        GaguePage {
            //anchors.fill: parent
        }

        DataGraph {
            //anchors.fill: parent
        }

    }
}
