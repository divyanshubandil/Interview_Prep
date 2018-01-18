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

# Scheduling

- Preemptible ( Processor or I/O) and Non Preemptible resources ( file space, terminal and maybe memory) resources
- FCFS - use ready queue
- Round Robin Scheduling
- Shortest Job First or Shortest time to completion first
- Past behaviour is a good indicator for future behaviours

## Priority Based Scheduling

- Priorities: most real schedulers support a priority for each thread:
  - Always run the thread with highest priority.
  - In case of tie, use round-robin among highest priority threads
  - Use priorities to implement various scheduling policies (e.g. approximate STCF)
- Exponential Queues (or Multi-Level Feedback Queues): attacks both efficiency and response time problems.
  - One ready queue for each priority level.
  - Lower-priority queues have larger time slices (time slice doubles with each reduction in priority)
  - Newly runnable thread starts in highest priority queue
  - If it reaches the end of its time slice without blocking it moves to the next lower queue.
  - Result: I/O-bound threads stay in the highest-priority queues, CPU-bound threads migrate to lower-priority queues

## Multiprocessor Scheduling

- Multiprocessor scheduling is mostly the same as uniprocessor scheduling:
  - Share the scheduling data structures among all of the cores
  - Run the k highest-priority threads on the k cores.
  - When a thread becomes runnable, see if its priority is higher than the lowest-priority thread currently running. If so, preempt that thread.
  - However, a single ready queue can result in contention problems if there are lots of cores.

- 2 special issues for multiprocessors:
- Core affinity:
  - Once a thread has been running on a particular core it is expensive to move it to a different core (hardware caches will have to be reloaded).
  - Multiprocessor schedulers typically try to keep a thread on the same core as much as possible to minimize these overheads.
- Gang scheduling:
  - If the threads within a process are communicating frequently, it won't make sense to run one thread without the others: it will just block immediately on communication with another thread.
  - Solution: run all of the threads of a process simultaneously on different cores, so they can communicate more efficiently.
  - This is called gang scheduling.
  - Even if a thread blocks, it may make sense to leave it loaded on its core, on the assumption that it will unblock in the near future.


# Dynamic Storage Management

- Stack
  - Used when memory allocation and freeing are partially predictable: memory is freed in opposite order from allocation.
  - A stack-based organization keeps all the free space together in one place.
  - tree traversal, expression evaluation, top-down recursive descent parsers, etc.
- Heap
  - Used when memory allocation and freeing are unpredictable
  - Fragmentation: inefficient use of memory because of lots of small holes.
  - Best fit: keep linked list of free blocks, search the whole list on each allocation, choose block that comes closest to matching the needs of the allocation, save the excess for later. During release operations, merge adjacent free blocks.
  - First fit: just scan list for the first hole that is large enough. Free excess. Also merge on releases. Most first fit implementations are rotating first fit.
  - Bit map: alternate representation of the free list, useful if storage comes in fixed-size chunks (e.g. disk blocks).
    - Keep a large array of bits, one for each chunk.
    - If bit is 0 it means chunk is in use, if bit is 1 it means chunk is free.
  - Pools: keep a separate linked list for each popular size.
    - Allocation is fast, no fragmentation.
- Storage Reclamation
  - Dangling pointers
  - Memory Leaks
  - Reference Counts
  - Garbage Collection
  - Garbage Collection - Mark and Copy
    - Must be able to find all objects.
    - Must be able to find all pointers to objects.
    - Pass 1: mark. Go through all statically-allocated and procedure-local variables, looking for pointers (roots). Mark each object pointed to, and recursively mark all objects it points to. The compiler has to cooperate by saving information about where the pointers are within structures.
    - Pass 2: copy and compact. Go through all objects, copy live Objects into contiguous memory; then free any remaining space.

# Virtual Memory

