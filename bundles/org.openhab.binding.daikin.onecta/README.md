# Onecta Binding


With the newer Daikin units it is no longer possible to control them directly. The units can only be connected to the Daikin cloud called Onecta.
The units can then 'only' be controlled with the Onecta app on a phone or tablet.
This binding makes it possible to still control the units with OpenHAB. It's now done by connecting the binding to Daikin's Onecta.
The unit information is then received from the Daikin cloud just like the app. Commands to the units also run via the Daikin Cloud.
Older units can also be controlled with this binding as long as they are registered in Onecta.

<img alt="discovery pictures"  src="doc/Onecta1.png" width="250"/>
<img alt="discovery pictures"  src="doc/Onecta2.png" width="250"/>
<img alt="discovery pictures"  src="doc/Things.png" width="500"/>


## Supported Things

Basically all devices connected to Daikin Onecta cloud could be connected with the binding.

- `bridge`: Ensures the connection to Onecta cloud and the recognition of connected units

## Discovery

Once the bridge is connected to Daikin Onecta it will receive all data from the connected units. The available units will appear in the inbox. From here they can be added as a new thing

## Binding Configuration


| Name             | Type    | Description                                                    | Default | Required | Advanced |
|------------------|---------|----------------------------------------------------------------|---------|----------|----------|
| UserId           | text    | UserId / Emailadress Onecta account                            | N/A     | yes      | no       |
| Password         | text    | Password to access Onecta account                              | N/A     | yes      | no       |
| refreshInterval  | integer | Interval the device is polled in sec.                          | 60      | yes      | no       |
| Onecta Refresh token  | text    | Is filled automaticly with Refresh token to speedup connection | N/A      | No       | no       |
| Drop Raw-data    | boolean | If true raw JSon Data is dropt in log file                     | false   | yes      | no       |
| Stub datafile  | text    | Path to JSon stubdata file only for debugging                  | N/A     | no       | no       |


## Thing Configuration

_Describe what is needed to manually configure a thing, either through the UI or via a thing-file._
_This should be mainly about its mandatory and optional configuration parameters._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

### `sample` Thing Configuration

| Name            | Type    | Description                           | Default | Required | Advanced |
|-----------------|---------|---------------------------------------|---------|----------|----------|
| hostname        | text    | Hostname or IP address of the device  | N/A     | yes      | no       |
| password        | text    | Password to access the device         | N/A     | yes      | no       |
| refreshInterval | integer | Interval the device is polled in sec. | 600     | no       | yes      |

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

| Channel | Type   | Read/Write | Description                 |
|---------|--------|------------|-----------------------------|
| control | Switch | RW         | This is the control channel |

## Full Example

_Provide a full usage example based on textual configuration files._
_*.things, *.items examples are mandatory as textual configuration is well used by many users._
_*.sitemap examples are optional._

### Thing Configuration

```java
Example thing configuration goes here.
```
### Item Configuration

```java
Example item configuration goes here.
```

### Sitemap Configuration

```perl
Optional Sitemap configuration goes here.
Remove this section, if not needed.
```

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
