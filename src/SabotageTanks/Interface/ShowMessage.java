/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Interface;

import javax.swing.JOptionPane;

/**
 *
 * @author ytokmakov
 */
public class ShowMessage {
    
    public static void nameIsEmpty(){ showErrorMessage("Заполните имя игрока"); }
    public static void serverConnectFail(){ showErrorMessage("Не удалось подключиться к серверу"); }
    public static void portIsEmpty(){ showErrorMessage("Введите порт"); }
    public static void ipIsEmpty(){ showErrorMessage("Введите ip"); }
    public static void connectingServerFail(){ showErrorMessage("Не удалось подключиться к серверу"); }
    public static void startingServerFail(){ showErrorMessage("Не удалось создать сервер"); }
    public static void initiatingLogFail(){ showErrorMessage("Не удалось создать лог файл"); }    
    
    private static void showErrorMessage(String message)
    {
        JOptionPane.showMessageDialog(null, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    
}
