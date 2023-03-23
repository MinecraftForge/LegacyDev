/*
 * LegacyDev
 * Copyright (c) 2016-2023.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.minecraftforge.legacydev;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainClient extends Main {
    public static void main(String[] args) throws Exception {
        new MainClient().start(args);
    }

    @Override
    protected void handleNatives(String path) {
        String paths = System.getProperty("java.library.path");

        if (paths == null || paths.isEmpty())
            paths = path;
        else
            paths += File.pathSeparator + path;

        System.setProperty("java.library.path", paths);

        // hack the classloader now.
        try {
            final Method initializePathMethod = ClassLoader.class.getDeclaredMethod("initializePath", String.class);
            initializePathMethod.setAccessible(true);
            final Object usrPathsValue = initializePathMethod.invoke(null, "java.library.path");
            final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
            usrPathsField.setAccessible(true);
            usrPathsField.set(null, usrPathsValue);
        }
        catch(Throwable t) {};
    }

    @Override
    protected Map<String, String> getDefaultArguments() {
        Map<String, String> ret = new LinkedHashMap<>();
        ret.put("version", getenv("MC_VERSION"));
        ret.put("assetIndex", getenv("assetIndex"));
        ret.put("assetsDir", getenv("assetDirectory"));
        ret.put("accessToken", "Forge");
        ret.put("userProperties", "[]");
        ret.put("username", null);
        ret.put("password", null);
        return ret;
    }
}
