package com.basic.core

import org.apache.commons.lang.builder.HashCodeBuilder

class BaseUserBaseRole implements Serializable {

	BaseUser baseUser
	BaseRole baseRole

	boolean equals(other) {
		if (!(other instanceof BaseUserBaseRole)) {
			return false
		}

		other.baseUser?.id == baseUser?.id &&
			other.baseRole?.id == baseRole?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (baseUser) builder.append(baseUser.id)
		if (baseRole) builder.append(baseRole.id)
		builder.toHashCode()
	}

	static BaseUserBaseRole get(long baseUserId, long baseRoleId) {
		find 'from BaseUserBaseRole where baseUser.id=:baseUserId and baseRole.id=:baseRoleId',
			[baseUserId: baseUserId, baseRoleId: baseRoleId]
	}

	static BaseUserBaseRole create(BaseUser baseUser, BaseRole baseRole, boolean flush = false) {
		new BaseUserBaseRole(baseUser: baseUser, baseRole: baseRole).save(flush: flush, insert: true)
	}

	static boolean remove(BaseUser baseUser, BaseRole baseRole, boolean flush = false) {
		BaseUserBaseRole instance = BaseUserBaseRole.findByBaseUserAndBaseRole(baseUser, baseRole)
		if (!instance) {
			return false
		}

		instance.delete(flush: flush)
		true
	}

	static void removeAll(BaseUser baseUser) {
		executeUpdate 'DELETE FROM BaseUserBaseRole WHERE baseUser=:baseUser', [baseUser: baseUser]
	}

	static void removeAll(BaseRole baseRole) {
		executeUpdate 'DELETE FROM BaseUserBaseRole WHERE baseRole=:baseRole', [baseRole: baseRole]
	}

	static mapping = {
		id composite: ['baseRole', 'baseUser']
		version false
	}
}
