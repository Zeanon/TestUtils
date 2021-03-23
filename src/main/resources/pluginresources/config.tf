################################################################################################################
#||==========================================================================================================||#
#||                                                                                                          ||#
#||                                          TestUtils Config-File                                           ||#
#||                                             Plugin by Zeanon                                             ||#
#||                            Config-Management-System: StorageManager by Zeanon                            ||#
#||                                                                                                          ||#
#||==========================================================================================================||#
################################################################################################################

################################################################################################################
# For Syntax-Highlighting for the config file or information about the syntax, please visit                    #
# https://github.com/Zeanon/StorageManager                                                                     #
#                                                                                                              #
# The plugin will reload the config on it's own when the config file gets updated                              #
################################################################################################################


Plugin Version = ${project.version}

# Define the maximum amount of undos possible for the "/tu undo" command
Max History = 10

# Should the plugin automatically reload the Server after it got updated?
# If you have PlugMan installed, it will only reload itself, not the whole server.
# PlugMan: https://dev.bukkit.org/projects/plugman
Automatic Reload = true

Backups {
    # Maximum amount of manual backups per player per region
    manual {
        # enable manual backups.
        enable = true

        # how many manual backups should be kept at max?
        amount = 10
    }

    # Maximum amount of backups per region created on startup
    startup {
        # enable backups when the plugin is getting enabled.
        enable = true

        # how many startup backups should be kept at max?
        amount = 10
    }

    # Maximum amount of backups per region created on an hourly basis
    hourly {
        # enable hourly backups.
        enable = true

        # how many hourly backups should be kept at max?
        amount = 24
    }

    daily {
        # enable daily backups at 00:00.
        enable = true

        # how many daily backups should be kept at max?
        amount = 7
    }
}
