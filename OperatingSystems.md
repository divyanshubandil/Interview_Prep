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