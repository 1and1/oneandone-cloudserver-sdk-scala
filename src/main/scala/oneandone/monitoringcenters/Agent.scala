package oneandone.monitoringcenters

case class Agent(
    agentInstalled: Boolean,
    monitoringNeedsAgent: Boolean,
    missingAgentAlert: Boolean
) {}
