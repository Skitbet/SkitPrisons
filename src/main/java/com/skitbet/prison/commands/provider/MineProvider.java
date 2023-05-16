package com.skitbet.prison.commands.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandArgumentException;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import com.skitbet.prison.PrisonPlugin;
import com.skitbet.prison.mine.Mine;
import com.skitbet.prison.mine.MineManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class MineProvider extends DrinkProvider<Mine> {
    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public Mine provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        Mine mine = PrisonPlugin.getInstance().getMineManager().getMines().stream()
                .filter(mine1 -> mine1.getName().equals(name))
                .findFirst()
                .orElse(null);
        if (mine == null) {
            throw new CommandExitMessage("§cThe mine §e" + name + "§c could not be found!");
        }

        return mine;
    }

    @Override
    public String argumentDescription() {
        return "The mine provided";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return PrisonPlugin.getInstance().getMineManager().getMines().stream()
                .map(Mine::getName)
                .collect(Collectors.toList());
    }
}
