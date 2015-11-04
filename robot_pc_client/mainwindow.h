#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <tcp_trans.h>
namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    car_tcp_struct tcp_send;
    car_tcp_struct tcp_rec;
    car_tcp_struct tcp_img;
    QTimer *recTimer;
    ~MainWindow();

private slots:
    void on_BtAddInit_clicked();

    void on_BtDelInit_clicked();

    void on_BtAddSet_clicked();

    void on_BtDelDel_clicked();

    void on_BtInit_clicked();

    void recTcp(void);

    void on_BtSet_clicked();

private:
    Ui::MainWindow *ui;
};

#endif // MAINWINDOW_H
