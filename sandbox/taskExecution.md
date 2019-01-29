## Javascript Task Pattern

Basically, you want to be able to separate the work from the response to the work.
This can be structured so that you can issue lifecycle events separately from handling those events.

In the simplest case, you will have two simple instances:

### 1. Stateful `TaskInvocation` object
- holds any custom stateful information for this invocation.
- custom callbacks representing lifecycle events may be modeled as simple functions on the `taskInvocation` object.

> Note:
> - Use a unique invocation object for each invocation
> - It may be convenient to use a factory to generate `taskInvocation` instances, but it may not be necessary, dependending on the complexity of the case.

### 2. Stateless `work` function
- this is the business functionality
- work receives the `taskInvocation` instance for this invocation.
- the `taskInvocation` is accessible for any part of the defined work.
- custom callbacks on the `taskInvocation` object may be called to indicate lifecycle events.

If the work requires the ability to be cancelled, whether by automatic timeout or by user input, this is best thought of as **Another work function**.  It may use the original `taskInvocation` as a source for contextual data.  The `cancellationWorkFunction` may also issue cancellation lifecycle events to the `taskInvocation`. This means that the `taskInvocation` would contain happy path lifecycle events and cancellation lifecycle events.

### 3. Stateless `cancelWork` function
- this cancellation function will attempt to "cancel" the work being done by the `work` function.  What this means will, of course, be specific to the actual work being done by the `work` function.
- the function can invoke as many custom lifecycle events on the `taskInvocation` as it likes.

> For example, the original work being executed may not be able to be cancelled.  In that case, "cancellation" probably means to ignore all responses invoked from the `work` function.

## Invocation

Invoke the task by creating a `taskInvocation` instance and passing it to the `work` function.  This function will immediately return, but the task's lifecycle will be properly managed by the interactions you have prepared.

## Writing It

Generally, you can write the `taskInvocation` lifecycle events at the same time as the `work` function.  In this way, you can construct lifecycle events as they arise.

The first pair of events might be a `started()` event and a `completed()` event. the `started()` event is invoked at the beginning of the work, and the `completed()` event is invoked at the conclusion of the work being initiated in the `work` function.  This may actually take place in a different function that is invoked as a callback from another asynchronous function, such as an AJAX call.

Failure modes may be developed in parallel as they arise.

Additionally, state can be developed in parallel as the task is developed.

So this means you would probably develop the lifecycle as:

`STARTING => STARTED => COMPLETED` with `started()` and `completed()` events to get a basic happy path going.

Next you can add various failure mode events, such as `badData()` or `networkDown()`, or just `failed()`.  Ultimately, these are going to affect batch status like this: `STARTED => FAILED`.  Meanwhile the various lifecycle events will contribute appropriate results to the result in the context.

Next you can add interruption mode events: `cancelling()`, `stopped()` and `timedOut()`, for example.
These will correspond to status codes `STARTED => STOPPING => STOPPED`.
Of course there may also be appropriate result objects and exit codes persisted to correspond with these statuses.

It makes sense to do `canceled()` first.  This is because `timedOut()` implies that you probably also want to perform a cancellation once the time has expired.

So for cancellation, you first need to write the cancellation work function.  It will issue a `cancelling()` event.  When the cancellation is confirmed, it will issue a `stopped()` event.  This is the happy path for cancellation.

Then you can develop the failure modes of a cancellation attempt.  It will correspond to status codes `STARTED => STOPPING => UNKNOWN`.  The cancellation work task would issue various events such as `stopFailed()` or `stopTimedOut()`.  The `taskInvocation` object would respond by setting the appropriate exit code and result object.

Then you can add other entry points to a cancellation event.
For example: a `timedOut()` event issued before a cancellation work function is invoked.  If the cancellation is successful, a `stopped()` event would still be issued by the cancellation work function as always.



## Understanding Task State

Spring Batch thinks of state as being explicitly limited in this way:

```
ABANDONED 
COMPLETED
FAILED 
STARTED 
STARTING 
STOPPED 
STOPPING 
UNKNOWN 
```

Note that it does not include the concept of timeout, because timeout is just another type of failure.  It might be represented by a `TimeoutException` rather than an `RuntimeException`.

It looks like Spring thinks of statuses around start and stop events.  There is always an outcome.  Whether or not that outcome represents a "failure" to the business must be decided by business logic, not framework logic.

So around the need of STARTING a task, there are statuses:
`STARTING`, and `STARTED`.  If there is a failure at any point here... `FAILED`
If it finished running and produced an outcome, then it is `COMPLETED`, else `FAILED`.

So around the need of STOPPING a task, there are statuses:
`STOPPING`, and `STOPPED`.  IF there is a failure at any point here... ???

`ABANDONED` means finished processing, but is considered a business failure, and should not be retried ever.  This could be significant.

`FAILED` must mean, then, that the task finished processing, but is considered a business failure.

`UNKNOWN` at least represents failures in the Spring Batch framework itself, such as encountering an error in saving a step's metadata.

javax.batch does not have an `UNKNOWN` status.

But what is the initial state???  It is `STARTING`.  The entity does not HAVE a state in the database prior to STARTING.  So in our case, that could be a null state?

So we could have:

```
// bag to hold results
// any of these:
taskContext: {
    // any of...
    status: [
        "starting",
        "started",
        "completed",
        "failed",
        "stopping",
        "stopped"
    ]

    // custom exit codes...any of...
    exitCode: [
        "unknown",
        "completed",
        "failed",
        "timedOut",
        "canceled",
        "cancelFailed",
        "cancelTimedOut"
    ]

    // could be a business object, or an error
    // "undefined" can represent "UNKNOWN" in spring batch world
    "result": {},
}
```

In a react component situation...the invocation context would HAVE a react component, AND the react component would HAVE the invocation context!!  But all I need it for is to tell the component to update!  So a function would be fine.  Then I'm not leaking the info.