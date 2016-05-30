import QtQuick 2.4
import QtQuick.Extras 1.4

Item {
    id: rectangle1

    ToggleButton {
        id: hazardButton
        height: parent.height/3
        text: "Hazard"
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.top: parent.top
        onCheckedChanged: {
            master.signalLightState = checked ? 3 : 0;
            leftTurnSignal.checked = checked
            rightTurnSignal.checked = checked
        }
    }

    Item {
        id: rectangle2
        height: parent.height/3
        anchors.right: parent.right
        anchors.left: parent.left
        anchors.bottom: parent.bottom
        anchors.top: rectangle3.bottom

        ToggleButton {
            id: highBeamsButton
            text: "High"
            anchors.left: parent.horizontalCenter
            anchors.bottom: parent.bottom
            anchors.top: parent.top
            checkable: lowBeamsButton.checked
            enabled: lowBeamsButton.checked
            onCheckedChanged: {
                master.headLightState = checked ? 2 : 1;
            }

        }

        ToggleButton {
            id: lowBeamsButton
            text: "Low"
            anchors.right: parent.horizontalCenter
            anchors.bottom: parent.bottom
            anchors.top: parent.top
            onCheckedChanged: {
                master.headLightState = checked ? 1 : 0;
            }
        }

    }

    Item {
        id: rectangle3
        height: parent.height/3
        anchors.top: hazardButton.bottom
        anchors.left: parent.left
        anchors.right: parent.right

        ToggleButton {
            id: leftTurnSignal
            text: "Left"
            anchors.right: parent.horizontalCenter
            anchors.bottom: parent.bottom
            anchors.top: parent.top
            checkable: master.signalLightState != 3
            onCheckedChanged: {
                if(master.signalLightState != 3)
                    master.signalLightState = checked ? 2 : 0;
            }
        }

        ToggleButton {
            id: rightTurnSignal
            text: "Right"
            anchors.left: parent.horizontalCenter
            anchors.bottom: parent.bottom
            anchors.top: parent.top
            checkable: master.signalLightState != 3
            onCheckedChanged: {
                if(master.signalLightState != 3)
                    master.signalLightState = checked ? 1 : 0;
            }
        }
    }
    Connections {
        target: master
        onSignalLightStateChanged:  {
            var tmp = master.signalLightState;
            if(tmp == 3) {
                hazardButton.checked = true
            }
            else if(tmp == 2) {
                rightTurnSignal.checked = false
                leftTurnSignal.checked = true
            }
            else if(tmp == 1) {
                leftTurnSignal.checked = false
                rightTurnSignal.checked = true
            }
            else {
                hazardButton.checked = false
                leftTurnSignal.checked = false
                rightTurnSignal.checked = false
            }
        }
        onHeadLightStateChanged: {
            var tmp = master.headLightState;
            if(tmp == 2) {
                lowBeamsButton.checked = true
                highBeamsButton.checked = true
            }
            else if(tmp == 1) {
                lowBeamsButton.checked = true
                highBeamsButton.checked = false
            }
            else {
                highBeamsButton.checked = false
                lowBeamsButton.checked = false
            }
        }
    }
}
