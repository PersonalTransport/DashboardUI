#include <QApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include "Master.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    Master m;

    if(l_flg_tst_q_axis_current_low()) {
        auto q_low = l_u16_rd_q_axis_current_low();
        auto q_high = l_u16_rd_q_axis_current_high();
        int32_t q_current_fixed = (int32_t(q_high) << 16) | int32_t(q_low);
        double q_current = double(q_current_fixed) / 65536.0;
    }

    qmlRegisterUncreatableType<Dataset>("com.ptransportation",1,0,"Dataset","");

    QQmlApplicationEngine engine;
    engine.rootContext()->setContextProperty("master",&m);
    engine.load(QUrl(QStringLiteral("qrc:/main.qml")));

    return app.exec();
}
