#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include "Master.h"
#include <thread>
#include <QTimer>

int main(int argc, char *argv[])
{
    QGuiApplication app(argc, argv);
    Master m;

    QQmlApplicationEngine engine;
    engine.rootContext()->setContextProperty("master",&m);
    engine.load(QUrl(QStringLiteral("qrc:/main.qml")));

    return app.exec();
}
