/*
 * Java-systemd implementation
 * Copyright (c) 2016 Markus Enax
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of either the GNU Lesser General Public License Version 2 or the
 * Academic Free Licence Version 2.1.
 *
 * Full licence texts are included in the COPYING file with this program.
 */

package org.systemd;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.freedesktop.DBus.Introspectable;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.exceptions.DBusException;
import org.systemd.interfaces.UnitInterface;
import org.systemd.types.Condition;
import org.systemd.types.Job;
import org.systemd.types.LoadError;

public abstract class Unit extends InterfaceAdapter {

    public static final String SERVICE_NAME = Systemd.SERVICE_NAME + ".Unit";
    public static final String OBJECT_PATH = Systemd.OBJECT_PATH + "/unit/";

    public enum Who {
        MAIN("main"),
        CONTROL("control"),
        ALL("all");

        private final String value;

        private Who(final String value) {
            this.value = value;
        }

        public final String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public enum Mode {
        REPLACE("replace"),
        FAIL("fail"),
        ISOLATE("isolate"),
        IGNORE_DEPENDENCIES("ignore-dependencies"),
        IGNORE_REQUIREMENTS("ignore-requirements");

        private final String value;

        private Mode(final String value) {
            this.value = value;
        }

        public final String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public static class Property extends InterfaceAdapter.Property {

        public static final String ACTIVE_ENTER_TIMESTAMP = "ActiveEnterTimestamp";
        public static final String ACTIVE_ENTER_TIMESTAMP_MONOTONIC = "ActiveEnterTimestampMonotonic";
        public static final String ACTIVE_EXIT_TIMESTAMP = "ActiveExitTimestamp";
        public static final String ACTIVE_EXIT_TIMESTAMP_MONOTONIC = "ActiveExitTimestampMonotonic";
        public static final String ACTIVE_STATE = "ActiveState";
        public static final String AFTER = "After";
        public static final String ALLOW_ISOLATE = "AllowIsolate";
        public static final String ASSERT_RESULT = "AssertResult";
        public static final String ASSERT_TIMESTAMP = "AssertTimestamp";
        public static final String ASSERT_TIMESTAMP_MONOTONIC = "AssertTimestampMonotonic";
        public static final String ASSERTS = "Asserts";
        public static final String BEFORE = "Before";
        public static final String BINDS_TO = "BindsTo";
        public static final String BOUND_BY = "BoundBy";
        public static final String CAN_ISOLATE = "CanIsolate";
        public static final String CAN_RELOAD = "CanReload";
        public static final String CAN_START = "CanStart";
        public static final String CAN_STOP = "CanStop";
        public static final String CONDITION_RESULT = "ConditionResult";
        public static final String CONDITION_TIMESTAMP = "ConditionTimestamp";
        public static final String CONDITION_TIMESTAMP_MONOTONIC = "ConditionTimestampMonotonic";
        public static final String CONDITIONS = "Conditions";
        public static final String CONFLICTED_BY = "ConflictedBy";
        public static final String CONFLICTS = "Conflicts";
        public static final String CONSISTS_OF = "ConsistsOf";
        public static final String DEFAULT_DEPENDENCIES = "DefaultDependencies";
        public static final String DESCRIPTION = "Description";
        public static final String DOCUMENTATION = "Documentation";
        public static final String DROP_IN_PATHS = "DropInPaths";
        public static final String FOLLOWING = "Following";
        public static final String FRAGMENT_PATH = "FragmentPath";
        public static final String ID = "Id";
        public static final String IGNORE_ON_ISOLATE = "IgnoreOnIsolate";
        public static final String INACTIVE_ENTER_TIMESTAMP = "InactiveEnterTimestamp";
        public static final String INACTIVE_ENTER_TIMESTAMP_MONOTONIC = "InactiveEnterTimestampMonotonic";
        public static final String INACTIVE_EXIT_TIMESTAMP = "InactiveExitTimestamp";
        public static final String INACTIVE_EXIT_TIMESTAMP_MONOTONIC = "InactiveExitTimestampMonotonic";
        public static final String JOB = "Job";
        public static final String JOB_TIMEOUT_ACTION = "JobTimeoutAction";
        public static final String JOB_TIMEOUT_REBOOT_ARGUMENT = "JobTimeoutRebootArgument";
        public static final String JOB_TIMEOUT_USEC = "JobTimeoutUSec";
        public static final String JOINS_NAMESPACE_OF = "JoinsNamespaceOf";
        public static final String LOAD_ERROR = "LoadError";
        public static final String LOAD_STATE = "LoadState";
        public static final String NAMES = "Names";
        public static final String NEED_DAEMON_RELOAD = "NeedDaemonReload";
        public static final String ON_FAILURE = "OnFailure";
        public static final String ON_FAILURE_JOB_MODE = "OnFailureJobMode";
        public static final String PART_OF = "PartOf";
        public static final String PROPAGATES_RELOAD_TO = "PropagatesReloadTo";
        public static final String REFUSE_MANUAL_START = "RefuseManualStart";
        public static final String REFUSE_MANUAL_STOP = "RefuseManualStop";
        public static final String RELOAD_PROPAGATED_FROM = "ReloadPropagatedFrom";
        public static final String REQUIRED_BY = "RequiredBy";
        public static final String REQUIRES = "Requires";
        public static final String REQUIRES_MOUNTS_FOR = "RequiresMountsFor";
        public static final String REQUISITE = "Requisite";
        public static final String REQUISITE_OF = "RequisiteOf";
        public static final String SOURCE_PATH = "SourcePath";
        public static final String STOP_WHEN_UNNEEDED = "StopWhenUnneeded";
        public static final String SUB_STATE = "SubState";
        public static final String TRANSIENT = "Transient";
        public static final String TRIGGERED_BY = "TriggeredBy";
        public static final String TRIGGERS = "Triggers";
        public static final String WANTED_BY = "WantedBy";
        public static final String WANTS = "Wants";

        private Property() {
            super();
        }

        public static final String[] getAllNames() {
            return getAllNames(Property.class);
        }

    }

    protected final String name;

    private final Properties properties;

    protected Unit(final DBusConnection dbus, final UnitInterface iface, final String name) throws DBusException {
        super(dbus, iface);

        this.name = name;
        this.properties = Properties.create(dbus, iface.getObjectPath(), SERVICE_NAME);
    }

    public static String normalizeName(final String name, final String suffix) {
        return name.endsWith(suffix) ? name : name + suffix;
    }

    @Override
    public UnitInterface getInterface() {
        return (UnitInterface) super.getInterface();
    }

    public final Properties getUnitProperties() {
        return properties;
    }

    public String introspect() throws DBusException {
        Introspectable intro = dbus.getRemoteObject(Systemd.SERVICE_NAME, getInterface().getObjectPath(), Introspectable.class);

        return intro.Introspect();
    }

    public void kill(final Who who, final int signal) {
        getInterface().kill(who.getValue(), signal);
    }

    public Path reload(final Mode mode) {
        return reload(mode.getValue());
    }

    public Path reload(final String mode) {
        return getInterface().reload(mode);
    }

    public Path reloadOrRestart(final Mode mode) {
        return reloadOrRestart(mode.getValue());
    }

    public Path reloadOrRestart(final String mode) {
        return getInterface().reloadOrRestart(mode);
    }

    public Path reloadOrTryRestart(final Mode mode) {
        return reloadOrTryRestart(mode.getValue());
    }

    public Path reloadOrTryRestart(final String mode) {
        return getInterface().reloadOrTryRestart(mode);
    }

    public void resetFailed() {
        getInterface().resetFailed();
    }

    public Path restart(final Mode mode) {
        return restart(mode.getValue());
    }

    public Path restart(final String mode) {
        return getInterface().restart(mode);
    }

    public void setProperties(final boolean runtime, final Map<String, Object> properties) {
        throw new UnsupportedOperationException();
    }

    public Path start(final Mode mode) {
        return start(mode.getValue());
    }

    public Path start(final String mode) {
        return getInterface().start(mode);
    }

    public Path stop(final Mode mode) {
        return stop(mode.getValue());
    }

    public Path stop(final String mode) {
        return getInterface().stop(mode);
    }

    public Path tryRestart(final Mode mode) {
        return tryRestart(mode.getValue());
    }

    public Path tryRestart(final String mode) {
        return getInterface().tryRestart(mode);
    }

    public long getActiveEnterTimestamp() {
        return properties.getLong(Property.ACTIVE_ENTER_TIMESTAMP);
    }

    public long getActiveEnterTimestampMonotonic() {
        return properties.getLong(Property.ACTIVE_ENTER_TIMESTAMP_MONOTONIC);
    }

    public long getActiveExitTimestamp() {
        return properties.getLong(Property.ACTIVE_EXIT_TIMESTAMP);
    }

    public long getActiveExitTimestampMonotonic() {
        return properties.getLong(Property.ACTIVE_EXIT_TIMESTAMP_MONOTONIC);
    }

    public String getActiveState() {
        return properties.getString(Property.ACTIVE_STATE);
    }

    public Vector<String> getAfter() {
        return properties.getVector(Property.AFTER);
    }

    public boolean isAllowIsolate() {
        return properties.getBoolean(Property.ALLOW_ISOLATE);
    }

    public boolean isAssertResult() {
        return properties.getBoolean(Property.ASSERT_RESULT);
    }

    public long getAssertTimestamp() {
        return properties.getLong(Property.ASSERT_TIMESTAMP);
    }

    public long getAssertTimestampMonotonic() {
        return properties.getLong(Property.ASSERT_TIMESTAMP_MONOTONIC);
    }

    public List<Condition> getAsserts() {
        return Condition.list(properties.getVector(Property.ASSERTS));
    }

    public Vector<String> getBefore() {
        return properties.getVector(Property.BEFORE);
    }

    public Vector<String> getBindsTo() {
        return properties.getVector(Property.BINDS_TO);
    }

    public Vector<String> getBoundBy() {
        return properties.getVector(Property.BOUND_BY);
    }

    public boolean isCanIsolate() {
        return properties.getBoolean(Property.CAN_ISOLATE);
    }

    public boolean isCanReload() {
        return properties.getBoolean(Property.CAN_RELOAD);
    }

    public boolean isCanStart() {
        return properties.getBoolean(Property.CAN_START);
    }

    public boolean isCanStop() {
        return properties.getBoolean(Property.CAN_STOP);
    }

    public boolean getConditionResult() {
        return properties.getBoolean(Property.CONDITION_RESULT);
    }

    public long getConditionTimestamp() {
        return properties.getLong(Property.CONDITION_TIMESTAMP);
    }

    public long getConditionTimestampMonotonic() {
        return properties.getLong(Property.CONDITION_TIMESTAMP_MONOTONIC);
    }

    public List<Condition> getConditions() {
        return Condition.list(properties.getVector(Property.CONDITIONS));
    }

    public Vector<String> getConflictedBy() {
        return properties.getVector(Property.CONFLICTED_BY);
    }

    public Vector<String> getConflicts() {
        return properties.getVector(Property.CONFLICTS);
    }

    public Vector<String> getConsistsOf() {
        return properties.getVector(Property.CONSISTS_OF);
    }

    public boolean isDefaultDependencies() {
        return properties.getBoolean(Property.DEFAULT_DEPENDENCIES);
    }

    public String getDescription() {
        return properties.getString(Property.DESCRIPTION);
    }

    public Vector<String> getDocumentation() {
        return properties.getVector(Property.DOCUMENTATION);
    }

    public Vector<String> getDropInPaths() {
        return properties.getVector(Property.DROP_IN_PATHS);
    }

    public String getFollowing() {
        return properties.getString(Property.FOLLOWING);
    }

    public String getFragmentPath() {
        return properties.getString(Property.FRAGMENT_PATH);
    }

    public String getId() {
        return properties.getString(Property.ID);
    }

    public boolean isIgnoreOnIsolate() {
        return properties.getBoolean(Property.IGNORE_ON_ISOLATE);
    }

    public long getInactiveEnterTimestamp() {
        return properties.getLong(Property.INACTIVE_ENTER_TIMESTAMP);
    }

    public long getInactiveEnterTimestampMonotonic() {
        return properties.getLong(Property.INACTIVE_ENTER_TIMESTAMP_MONOTONIC);
    }

    public long getInactiveExitTimestamp() {
        return properties.getLong(Property.INACTIVE_EXIT_TIMESTAMP);
    }

    public long getInactiveExitTimestampMonotonic() {
        return properties.getLong(Property.INACTIVE_EXIT_TIMESTAMP_MONOTONIC);
    }

    public Job getJob() {
        Object[] array = (Object[]) properties.getVariant(Property.JOB).getValue();

        return new Job(array);
    }

    public String getJobTimeoutAction() {
        return properties.getString(Property.JOB_TIMEOUT_ACTION);
    }

    public String getJobTimeoutRebootArgument() {
        return properties.getString(Property.JOB_TIMEOUT_REBOOT_ARGUMENT);
    }

    public long getJobTimeoutUSec() {
        return properties.getLong(Property.JOB_TIMEOUT_USEC);
    }

    public Vector<String> getJoinsNamespaceOf() {
        return properties.getVector(Property.JOINS_NAMESPACE_OF);
    }

    public LoadError getLoadError() {
    	Object[] array = (Object[]) properties.getVariant(Property.LOAD_ERROR).getValue();

    	return new LoadError(array);
    }

    public String getLoadState() {
        return properties.getString(Property.LOAD_STATE);
    }

    public Vector<String> getNames() {
        return properties.getVector(Property.NAMES);
    }

    public boolean isNeedDaemonReload() {
        return properties.getBoolean(Property.NEED_DAEMON_RELOAD);
    }

    public Vector<String> getOnFailure() {
        return properties.getVector(Property.ON_FAILURE);
    }

    public String getOnFailureJobMode() {
        return properties.getString(Property.ON_FAILURE_JOB_MODE);
    }

    public Vector<String> getPartOf() {
        return properties.getVector(Property.PART_OF);
    }

    public Vector<String> getPropagatesReloadTo() {
        return properties.getVector(Property.PROPAGATES_RELOAD_TO);
    }

    public boolean isRefuseManualStart() {
        return properties.getBoolean(Property.REFUSE_MANUAL_START);
    }

    public boolean isRefuseManualStop() {
        return properties.getBoolean(Property.REFUSE_MANUAL_STOP);
    }

    public Vector<String> getReloadPropagatedFrom() {
        return properties.getVector(Property.RELOAD_PROPAGATED_FROM);
    }

    public Vector<String> getRequiredBy() {
        return properties.getVector(Property.REQUIRED_BY);
    }

    public Vector<String> getRequires() {
        return properties.getVector(Property.REQUIRES);
    }

    public Vector<String> getRequiresMountsFor() {
        return properties.getVector(Property.REQUIRES_MOUNTS_FOR);
    }

    public Vector<String> getRequisite() {
        return properties.getVector(Property.REQUISITE);
    }

    public Vector<String> getRequisiteOf() {
        return properties.getVector(Property.REQUISITE_OF);
    }

    public String getSourcePath() {
        return properties.getString(Property.SOURCE_PATH);
    }

    public boolean isStopWhenUnneeded() {
        return properties.getBoolean(Property.STOP_WHEN_UNNEEDED);
    }

    public String getSubState() {
        return properties.getString(Property.SUB_STATE);
    }

    public boolean isTransient() {
        return properties.getBoolean(Property.TRANSIENT);
    }

    public Vector<String> getTriggeredBy() {
        return properties.getVector(Property.TRIGGERED_BY);
    }

    public Vector<String> getTriggers() {
        return properties.getVector(Property.TRIGGERS);
    }

    public Vector<String> getWantedBy() {
        return properties.getVector(Property.WANTED_BY);
    }

    public Vector<String> getWants() {
        return properties.getVector(Property.WANTS);
    }

    @Override
    public String toString() {
        return name;
    }

}
