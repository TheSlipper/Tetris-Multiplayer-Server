   **Tetris Multiplayer General Commands Manual**

**NAME**
       `list` - list all of the specified information and output it to
                the screen.

**SYNOPSIS**
       `list`
       `list -ci`

**DESCRIPTION**
       `list` - clears your screen if this is possible.
       
       The following options are available:
       
       -c       lists all available commands and their id number
       
       -i       lists all of the sessions' ip numbers and their unique id
       
       -o       lists all of the sessions' operating systems and their
                unique id

**EXIT STATUS**
    The *list* command exits with **true** on success (and prints the 
    listing to the screen), or with **false** if an error occurs.
