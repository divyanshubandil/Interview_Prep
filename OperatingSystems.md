https://web.stanford.edu/~ouster/cgi-bin/cs140-winter16/lectures.php
# Processes

## Processes
- Thread: Sequence of instructions
- Process : Group of threads with execution state

## Dispatching
- Hyperthreading:each core behaves as two cores, execute onethread while other is waiting on a cache miss 
- Process Control Block : execution state , scheduling, info about memory, files
- Thread state : running, blocked, ready
- Dispatcher : innermost part of OS, runs on each core, manages threads
- Context switch
- Traps : events occuring **in current thread** that cause a change of control into the OS- sys call, error, page fault
- Interrupts: events occuring **outside the current thread** that cause a state switch into the OS - keyboard input
- Dispatcher Thread Selection Method
  - Plan 0: search process table from front, run first ready thread.
  - Plan 1: link together the ready threads into a queue. Dispatcher grabs first thread from the queue. When threads become ready, insert at back of queue.
  - Plan 2: give each thread a priority, organize the queue according to priority. Or, perhaps have multiple queues, one for each priority class.

## Process Creation
- Create and initialize PCB, load code and data in memory, create first thread with call stack, provide inital values for "saved state", send thread to dispatcher, dispatcher "resumes" to start of new program
- UNIX - fork (copy of current process with one thread), exec(loads code and data in memory), waitpid ( waits for the given process to exit)

```c
    int pid = fork();
    if (pid == 0) {
        /* Child process  */
        exec("foo");
    } else {
        /* Parent process */
        waitpid(pid, &status, options);
    }
```

## Concurrency
- Independent thread : Does not share state
- Cooperating thread : Share state (Order of some operations is irrelevant)
- Atomic Operations
- Synchornization
- Critical Section : One thread executes at a time
- Mutual exclusion : mechanisms used to create critical sections.

### Peterson's Algorithm

```c
bool flag[0] = {false};
bool flag[1] = {false};
int turn;
P0:      flag[0] = true;
P0_gate: turn = 1;
         while (flag[1] == true && turn == 1)
         {
             // busy wait
         }
         // critical section
         ...
         // end of critical section
         flag[0] = false;
P1:      flag[1] = true;
P1_gate: turn = 0;
         while (flag[0] == true && turn == 0)
         {
             // busy wait
         }
         // critical section
         ...
         // end of critical section
         flag[1] = false;
```
