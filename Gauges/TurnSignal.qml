import QtQuick 2.0
import QtQuick.Extras 1.4
import QtQuick.Controls.Styles 1.4

StatusIndicator {
    property color activeColor: "green"
    color: activeColor
    SequentialAnimation on color {
        id: colorAnim
        ColorAnimation {
            from: activeColor
            to: "black"
            duration: 400
        }
        ColorAnimation {
            to: activeColor
            from: "black"
            duration: 200
        }
        loops: Animation.Infinite
    }
    onActiveChanged: {
        color = activeColor;
        if(active)
            colorAnim.restart();
        else
            colorAnim.stop();
    }
    MouseArea {
        anchors.fill: parent
        onClicked: {
            active = !active
        }
    }
}
