package booking_bot.commands;

import booking_bot.Bot;
import booking_bot.models.BookObject;
import booking_bot.models.Campus;
import booking_bot.models.Type;
import booking_bot.models.User;
import booking_bot.repositories.Controller;
import booking_bot.services.SendMessageService;
import org.telegram.telegrambots.facilities.filedownloader.TelegramFileDownloader;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddObject extends CommandParent {

    private final String commandName;
    private BookObject newObject;
    private Bot bot;

    public AddObject(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        super(sendMessageService, controller, commandContainer);
        this.commandName = "Добавить объект";
        commandContainer.add(commandName, this);
        newObject = new BookObject();
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    /*
    *   Поля унаследованные от родительского класса:
    *
    *   Long chatId - ID чата с пользователем
    *   String input - сообщение от пользователя
    *   String status - текущий статус, в начале он всегда "begin", а дальше надо перезаписывать на каждом шаге
    *   boolean isFinished - обозначает что весь сценарий прошли, в начале false, в самом конце меняем на true
     */
    @Override
    public boolean execute(Update update, boolean begin) {

        boolean isObject = false;

        prepare(update);
        if (begin) {
            status = "begin";
            statusMap.put(chatId, status);
        }

        System.out.println("- addObject: " + input + " status: " + status);

        if (status.equals("begin")) {

            isFinished = false;
//            List<String> campuses = getNames(controller.getCampus().findAll());
//            sendMessageService.sendWithKeyboard(chatId, "Выберите кампус", campuses);

//            statusMap.put(chatId, "Выбор категории");
//        } else if (status.equals("Выбор кампуса")) {
//            newObject.setCampus(controller.getCampus().findByName(input));
            newObject.setCampus(controller.getUser().findByTelegram(chatId).getCampus());
            List<String> buttons = getNames(controller.getType().findAll());
            sendMessageService.sendWithKeyboard(chatId, "Выберите категорию", buttons);

            statusMap.put(chatId, "Выбор категории");
        } else if (status.equals("Выбор категории")) {
            newObject.setType(controller.getType().findByName(input));

            sendMessageService.send(chatId, "Введите название");

            statusMap.put(chatId, "Ввод наименования объекта");
        } else if (status.equals("Ввод наименования объекта")) {
            newObject.setName(makeStr(input));

            List<BookObject> objectsList = controller.getBookingObject().findAll();

            for(BookObject obj : objectsList) {
                if (obj.getName().equals(makeStr(input)) && obj.getCampus().getName().equals(newObject.getCampus().getName())) {
                    isObject = true;
                }
            }
            if (isObject) {
                sendMessageService.send(chatId, "Объект " + newObject.getName() + " уже существует. Вы можете удалить или отредактировать его по кнопке \"Редактировать объект\".");
                statusMap.put(chatId, "begin");
                isFinished = true;
            }
            if (!isObject) {
                sendMessageService.send(chatId, "Введите описание");
                statusMap.put(chatId, "Ввод описания объекта");
            }

        } else if (status.equals("Ввод описания объекта")) {
            newObject.setDescription(input);
            List<String> button = new ArrayList<>();
            button.add("Сохранить без изображения");
            sendMessageService.sendWithKeyboard(chatId, "Загрузите изображение", button);
            statusMap.put(chatId, "загрузка изображения");
        } else if (status.equals("Пропустить")) {
            sendMessageService.send(chatId, "Готово\n" + newObject.getName() + "\n" + newObject.getDescription());
            controller.getBookingObject().save(newObject);
            statusMap.put(chatId, "begin");
            isFinished = true;

        } else if (status.equals("загрузка изображения")) {
            System.out.println("image upload - start");
            if (input != null && input.equals("Сохранить без изображения")) {
                sendMessageService.send(chatId, "Готово\n" + newObject.getName() + "\n" + newObject.getDescription());
                controller.getBookingObject().save(newObject);
                statusMap.put(chatId, "begin");
                isFinished = true;
            } else {
                Message message = update.getMessage();
                if (message.hasPhoto()) {
                    System.out.println("HAS PHOTO");

                    String getFileId = message.getPhoto().get(2).getFileId();
                    String filePath = "src/main/resources/images/" + getFileId + ".jpeg";
                    java.io.File file = new java.io.File(filePath);

                    GetFile getFile = new GetFile(message.getPhoto().get(2).getFileId());
                    try {
                        File f = bot.downloadFile(bot.execute(getFile), file);
                        newObject.setImage(filePath);

                        controller.getBookingObject().save(newObject);
                        sendMessageService.send(chatId, "Готово\n" + newObject.getName() + "\n" + newObject.getDescription());
                        System.out.println(newObject);
                        statusMap.put(chatId, "begin");
                        isFinished = true;
                    } catch (TelegramApiException e) {
                        sendMessageService.send(chatId, "Не получилось сохранить изображение, попробуйте другое");

                        e.printStackTrace();
                    }

                } else {
                    List<String> button = new ArrayList<>();
                    button.add("Сохранить без изображения");
                    sendMessageService.sendWithKeyboard(chatId, "Не получилось сохранить изображение, попробуйте еще раз", button);
                    statusMap.put(chatId, "загрузка изображения");
                }
            }
        }

        return isFinished;
    }

    public String makeStr(String inputStr){
        return (inputStr.substring(0,1).toUpperCase()+inputStr.substring(1).toLowerCase());
    }

    @Override
    public String getCommandName() {
        return commandName;
    }
}
