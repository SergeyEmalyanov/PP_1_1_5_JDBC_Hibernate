package jm.task.core.jdbc;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.Util;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        userService.createUsersTable();//1. Создание таблицы User(ов)
        String name;
        String lastName;
        for (byte i = 1; i <= 4; i++) {
            name = "Name" + i;
            lastName = "Lastname" + i;
            userService.saveUser(name, lastName, i);//2.Добавление 4 User(ов) в таблицу с данными на свой выбор.
            // После каждого добавления должен быть вывод в консоль ( User с именем – name добавлен в базу данных )
            System.out.println("User с именем – " + name + " добавлен в базу данных");
        }

        List<User> list = userService.getAllUsers(); //3.Получение всех User из базы и вывод в консоль
        // ( должен быть переопределен toString в классе User)
        for (User user : list) {
            System.out.println(user);
        }
        userService.cleanUsersTable();//4. Очистка таблицы User(ов)
        userService.dropUsersTable();//5. Удаление таблицы

        Util.closeSessionFactory();
    }
}
