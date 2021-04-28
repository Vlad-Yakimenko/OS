package ua.knu.cli.command;

import lombok.val;
import ua.knu.util.Constants;

public abstract class Command {

    public abstract boolean canProcess(String command);

    public abstract void process(String command);

    public abstract String getCommandSample();

    protected void verifyCorrectParametersAmount(int actualParametersAmount) {
        val correctParametersAmount = getCorrectParametersAmount();

        if (actualParametersAmount != correctParametersAmount) {
            throw new IllegalArgumentException(
                    String.format("Incorrect amount of parameters, expected %s, but actual %s",
                            correctParametersAmount, actualParametersAmount));
        }
    }

    private int getCorrectParametersAmount() {
        return getCommandSample().split(Constants.COMMAND_SEPARATOR).length;
    }
}
