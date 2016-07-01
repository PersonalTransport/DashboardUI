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
                var series = createSeries(ChartView.SeriesTypeLine,dataset.name,timeAxis,percentAxis)
                series.visible = false
            }
        }
        Timer {
            interval: 1000 / 24
            running: true
            repeat: true
            onTriggered: {
                for(var i=0;i<master.rowCount();++i) {
                    var dataset = master.getDataset(i);
                    var s = chartView.series(i);
                    dataset.update(s);
                }
                timeAxis.min = master.time - 500
                timeAxis.max = master.time
            }
        }
    }
}
