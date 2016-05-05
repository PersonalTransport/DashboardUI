import QtQuick 2.0
import "Gauges"


Flickable {
    id: flickable1
    anchors.fill: parent
    contentHeight: parent.height
    contentWidth: contentItem.children.length * parent.width

    interactive: true
    boundsBehavior: Flickable.DragAndOvershootBounds


    SmoothedAnimation on contentX {
        id: xAnimation
        to: 0
        velocity: 2*parent.width
    }

    Behavior on contentX {
        SpringAnimation {
            spring: 5
            damping: .2
        }
    }

    onMovementEnded: {
        var index = Math.round(contentX / (width))
        xAnimation.to = index * width
        xAnimation.start()
    }

    onWidthChanged: {
        for(var i=0;i<contentItem.children.length;++i) {
            contentItem.children[i].width = width
            contentItem.children[i].height = height
        }
    }
}

