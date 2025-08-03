package net.tom.dfdevkeyboardshortcuts;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DfDevKeyboardShortcuts implements ModInitializer {
	public static final String MOD_ID = "dfdevkeyboardshortcuts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Dev Keyboard Shortcuts");
	}
}
