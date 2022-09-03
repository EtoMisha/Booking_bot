package booking_bot.commands.impls;

import booking_bot.commands.CommandContainer;
import booking_bot.commands.CommandNames;
import booking_bot.commands.CommandParent;
import booking_bot.models.BookObject;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *   Поля унаследованные от родительского класса:
 *
 *   Long chatId - ID чата с пользователем
 *   String input - сообщение от пользователя
 *   String status - текущий статус, в начале он всегда "begin", а дальше надо перезаписывать на каждом шаге
 *   boolean isFinished - обозначает что весь сценарий прошли, в начале false, в самом конце меняем на true
 */
public class AddObject extends CommandParent {

    private final Map<Long, Status> statusMap;
    private Status currentStatus;
    private final String commandName;

    private final BookObject newBookObject;
    private static final String IMAGES_DIR = "\"/var/www/21sch/image/\"";

    public AddObject(BotService botService, Controller controller, CommandContainer commandContainer) {
        super(botService, controller, commandContainer);
        this.commandName = CommandNames.ADD_OBJECT.getText();
        commandContainer.add(commandName, this);
        statusMap = new HashMap<>();

        newBookObject = new BookObject();
    }

    @Override
    public boolean execute(Update update, boolean begin) {
        prepare(update);

        statusMap.putIfAbsent(chatId, Status.BEGIN);
        if (begin) {
            statusMap.put(chatId, currentStatus);
        }

        currentStatus = statusMap.get(chatId);

        if (currentStatus.equals(Status.BEGIN)) {
            isFinished = false;
            begin();
        } else if (currentStatus.equals(Status.SELECT_CATEGORY)) {
            selectCategory();
        } else if (currentStatus.equals(Status.OBJECT_NAME)) {
            saveObjectName();
        } else if (currentStatus.equals(Status.OBJECT_DESCRIPTION)) {
            saveObjectDescription();
        } else if (currentStatus.equals(Status.SKIP_IMAGE)) {
            skipImage();
        } else if (currentStatus.equals(Status.UPLOAD_IMAGE)) {
            uploadImage(update);
        }

        return isFinished;
    }

    private void begin() {
        newBookObject.setCampus(controller.getUser().findByTelegram(chatId).getCampus());
        List<String> buttons = getNames(controller.getType().findAll());
        botService.sendMessage(chatId, "Выберите категорию", buttons);
        statusMap.put(chatId, Status.SELECT_CATEGORY);
    }

    private void selectCategory() {
        newBookObject.setType(controller.getType().findByName(input));
        botService.sendMessage(chatId, "Введите название", null);
        statusMap.put(chatId, Status.OBJECT_NAME);
    }

    private void saveObjectName() {
        newBookObject.setName(makeStr(input));

        List<BookObject> objectsList = controller.getBookingObject().findAll();

        boolean isExist = false;
        for(BookObject obj : objectsList) {
            if (obj.getName().equals(makeStr(input)) && obj.getCampus().getName().equals(newBookObject.getCampus().getName())) {
                isExist = true;
            }
        }

        if (isExist) {
            botService.sendMessage(chatId, "Объект " + newBookObject.getName() + " уже существует. Вы можете удалить или отредактировать его по кнопке \"Редактировать объект\".", null);
            statusMap.put(chatId, Status.BEGIN);
            isFinished = true;
        } else {
            botService.sendMessage(chatId, "Введите описание", null);
            statusMap.put(chatId, Status.OBJECT_DESCRIPTION);
        }
    }

    private void saveObjectDescription() {
        newBookObject.setDescription(input);
        List<String> button = new ArrayList<>();
        button.add("Сохранить без изображения");
        botService.sendMessage(chatId, "Загрузите изображение", button);
        statusMap.put(chatId, Status.UPLOAD_IMAGE);
    }

    private void skipImage() {
        botService.sendMessage(chatId, "Готово\n" + newBookObject.getName() + "\n" + newBookObject.getDescription(), null);
        controller.getBookingObject().save(newBookObject);
        statusMap.put(chatId, Status.BEGIN);
        isFinished = true;
    }

    private void uploadImage(Update update) {
        if (input != null && input.equals("Сохранить без изображения")) {
            botService.sendMessage(chatId, "Готово\n" + newBookObject.getName() + "\n" + newBookObject.getDescription(), null);
            controller.getBookingObject().save(newBookObject);
            statusMap.put(chatId, Status.BEGIN);

        } else {
            Message message = update.getMessage();
            if (message.hasPhoto()) {

                String getFileId = message.getPhoto().get(2).getFileId();
                String filePath = IMAGES_DIR + getFileId + ".jpeg";
                java.io.File file = new java.io.File(filePath);

                GetFile getFile = new GetFile(message.getPhoto().get(2).getFileId());
                try {
                    botService.downloadPhoto(getFile, file);
                    newBookObject.setImage(filePath);

                    controller.getBookingObject().save(newBookObject);
                    botService.sendMessage(chatId, "Готово\n" + newBookObject.getName() + "\n" + newBookObject.getDescription(), null);

                    statusMap.put(chatId, Status.BEGIN);
                    isFinished = true;
                } catch (TelegramApiException e) {
                    botService.sendMessage(chatId, "Не получилось сохранить изображение, попробуйте другое", null);
                    e.printStackTrace();
                }

            } else {
                List<String> button = new ArrayList<>();
                button.add("Сохранить без изображения");
                botService.sendMessage(chatId, "Не получилось сохранить изображение, попробуйте еще раз", button);
                statusMap.put(chatId, Status.UPLOAD_IMAGE);
            }
        }
    }

    private String makeStr(String inputStr){
        return (inputStr.substring(0,1).toUpperCase() + inputStr.substring(1).toLowerCase());
    }

    private enum Status {
        BEGIN,
        SELECT_CATEGORY,
        OBJECT_NAME,
        OBJECT_DESCRIPTION,
        SKIP_IMAGE,
        UPLOAD_IMAGE,
    }

    @Override
    public String getCommandName() {
        return commandName;
    }
}

