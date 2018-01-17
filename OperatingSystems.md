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
- Busy waiting : while one thread waits for the other it consumes resources

#### Peterson's Algorithm (No Busy Waiting)

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

## Locks

- Basic Operation - acquire and release
- Producer Consumer

```c
char buffer[SIZE];
int count = 0, putIndex = 0, getIndex = 0;
struct lock l;
lock_init(&l);

void put(char c) {
    lock_acquire(&l);
    while (count == SIZE) {
        lock_release(&l);
        lock_acquire(&l);
    }
    count++;
    buffer[putIndex] = c;
    putIndex++;
    if (putIndex == SIZE) {
        putIndex = 0;
    }
    lock_release(&l);
}

char get() {
    char c;
    lock_acquire(&l);
    while (count == 0) {
        lock_release(&l);
        lock_acquire(&l);
    }
    count--;
    c = buffer[getIndex];
    getIndex++;
    if (getIndex == SIZE) {
        getIndex = 0;
    }
    lock_release(&l);
    return c;
}
```

### Conditional Variables

- wait(release lock, thread sleeps till condition), signal(wake one of the threads that condition is fulfilled), broadcast(wake up all threads)

```c
char buffer[SIZE];
int count = 0, putIndex = 0, getIndex = 0;
struct lock l;
struct condition dataAvailable;
struct condition spaceAvailable;

lock_init(&l);
cond_init(&dataAvailable);
cond_init(&spaceAvailable);

void put(char c) {
    lock_acquire(&l);
    while (count == SIZE) {
        cond_wait(&spaceAvailable, &l);
    }
    count++;
    buffer[putIndex] = c;
    putIndex++;
    if (putIndex == SIZE) {
        putIndex = 0;
    }
    cond_signal(&dataAvailable, &l);
    lock_release(&l);
}

char get() {
    char c;
    lock_acquire(&l);
    while (count == 0) {
        cond_wait(&dataAvailable, &l);
    }
    count--;
    c = buffer[getIndex];
    getIndex++;
    if (getIndex == SIZE) {
        getIndex = 0;
    }
    cond_signal(&spaceAvailable, &l);
    lock_release(&l);
    return c;
}
```

- Monitor - condition + lock + procedures (higher level) -> all objects can be used as monitors in Java
- Mutex - handle critical sections ( defined at OS level usually) - All locks can act as mutex in Java
- Semaphore - multiple locks , conceptually are able to signal to waiting thread that the lock is released

## Deadlock

```c
Thread A               Thread B
lock_acquire(l1);      lock_acquire(l2);
lock_acquire(l2);      lock_acquire(l1);
...                    ...
lock_release(l2);      lock_release(l1);
lock_release(l1);      lock_release(l2);
```

- Four conditions for deadlock:
  - Limited access: resources cannot be shared.
  - No preemption. Once given, a resource cannot be taken away.
  - Multiple independent requests: threads don't ask for resources all at once (hold resources while waiting).
  - A circularity in the graph of requests and ownership.

- Solution : Break the circularity : all threads **request resources in the same order**
