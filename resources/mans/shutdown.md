   **Tetris Multiplayer General Commands Manual**

**NAME**
       `shutdown` - close down the server at a given time

**SYNOPSIS**
       shutdown [-] [--] [-n] [--t] [--s] _time_

**DESCRIPTION**
       The `shutdown` utility provides an automated shutdown procedure for super-
       users to nicely notify users when the system is shutting down, saving them
       from system administrators, hackers, and gurus, who would otherwise not
       bother with such niceties.
       
       The following options are available:
       
       -n                   shuts down the system now
       
       --t [miliseconds]    shuts down the system after a given time (amount of
                            miliseconds)
       
       --s [status]         shuts down the system with a given status number
       
       now                  shuts down the system now       

**EXIT STATUS**
    The *shutdown* utility exits with **true** on success (and schedules
    the shutdown and the shutdown status), or exits with **false** if an error 
    occurs.
