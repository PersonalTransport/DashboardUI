import QtQuick 2.7
import QtCharts 2.0
import QtQuick.Controls 2.0
import QtQuick.Layouts 1.0

Rectangle {
    color: "lightgrey"

    ValueAxis {
        id: timeAxis
        min: 0
        max: 500
        titleText: "Time (ms)"
    }

    ValueAxis {
        id: percentAxis
        min: 0
        max: 100
        titleText: "Percent (%)"
    }

    ValueAxis {
        id: currentAxis
        min: -2
        max: 2
        titleText: "Current (A)"
    }

    ValueAxis {
        id: angleAxis
        min: 0
        max: 7.0*Math.PI/3.0
        titleText: "Angle (rad)"
    }

    function hideAllAxes() {
        percentAxis.visible = false;
        currentAxis.visible = false;
        angleAxis.visible = false;
    }

    function axisForUnit(unit) {
        if(unit === "%")
            return percentAxis
        else if(unit === "A")
            return currentAxis
        else if(unit === "rad")
            return angleAxis
    }

    ListView {
        id: sidePanel
        anchors.left: parent.left
        anchors.top: parent.top
        anchors.bottom: parent.bottom
        anchors.margins: 10
        implicitWidth: 250
        spacing: 10
        model: master
        delegate: CheckBox {
            text: name
            implicitWidth:sidePanel.implicitWidth
            onCheckedChanged: {
                chartView.series(index).visible = checked
            }
        }
    }
    ChartView {
        id: chartView
        anchors.right: parent.right
        anchors.left: sidePanel.right
        anchors.top: parent.top
        anchors.bottom: parent.bottom
        antialiasing: true
        Component.onCompleted: {
            for(var i=0;i<master.rowCount();++i) {
                var dataset = master.getDataset(i);
                var s = createSeries(ChartView.SeriesTypeLine,dataset.name,timeAxis,axisForUnit(dataset.unit))
                s.visible = false
            }
        }
        Timer {
            interval: 1000 / 24
            running: true
            repeat: true
            onTriggered: {
                hideAllAxes();
                for(var i=0;i<master.rowCount();++i) {
                    var dataset = master.getDataset(i);
                    var s = chartView.series(i);
                    dataset.update(s);
                    if(s.visible)
                        axisForUnit(dataset.unit).visible = true;
                }
                timeAxis.min = master.time - 500
                timeAxis.max = master.time
            }
        }
    }
}
