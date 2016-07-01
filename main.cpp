#include <QApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include "Master.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    Master m;

    qmlRegisterUncreatableType<Dataset>("com.ptransportation",1,0,"Dataset","");

    QQmlApplicationEngine engine;
    engine.rootContext()->setContextProperty("master",&m);
    engine.load(QUrl(QStringLiteral("qrc:/main.qml")));

    return app.exec();
}
