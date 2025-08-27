package com.arkflame.mineclans.api;

import java.util.UUID;

import com.arkflame.mineclans.api.results.clan.*;
import com.arkflame.mineclans.api.results.action.*;
import com.arkflame.mineclans.api.results.chat.*;
import com.arkflame.mineclans.api.results.member.*;
import com.arkflame.mineclans.api.results.misc.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.arkflame.mineclans.api.enums.RelationType;
import com.arkflame.mineclans.api.event.ClanEvent;
import com.arkflame.mineclans.api.models.Faction;
import com.arkflame.mineclans.api.models.FactionPlayer;

/**
 * Main API interface for interacting with the MineClans faction system.
 * Provides methods for managing factions, players, relations, claims, and other functionality.
 * <p>
 * For database-intensive operations, it's recommended to use the asynchronous versions
 * of these methods or wrap calls in CompletableFuture.
 *
 * @version 1.0
 * @author MineClans Development Team
 */
public interface MineClansAPI {

    // ==============================================
    // FACTION MANAGEMENT METHODS
    // ==============================================

    /**
     * Gets a player's faction
     *
     * @param player the player to check
     * @return the player's faction or null if not in a faction
     */
    Faction getFaction(Player player);

    /**
     * Gets a faction by name
     *
     * @param name the name of the faction
     * @return the faction or null if not found
     */
    Faction getFaction(String name);

    /**
     * Gets a faction by UUID
     *
     * @param id the UUID of the faction
     * @return the faction or null if not found
     */
    Faction getFaction(UUID id);

    /**
     * Creates a new faction
     *
     * @param player the player creating the faction
     * @param factionName the name for the new faction
     * @return result containing the creation status and the created faction
     */
    CreateResult createFaction(Player player, String factionName);

    /**
     * Disbands a faction
     *
     * @param player the player attempting to disband
     * @return result containing the disband status
     */
    DisbandResult disbandFaction(Player player);

    /**
     * Transfers faction ownership to another member
     *
     * @param player the current owner
     * @param newOwnerName the name of the new owner
     * @return result containing the transfer status
     */
    TransferResult transferOwnership(Player player, String newOwnerName);

    /**
     * Renames a faction
     *
     * @param player the player attempting to rename
     * @param newName the new name for the faction
     * @return result containing the rename status
     */
    RenameResult renameFaction(Player player, String newName);

    /**
     * Sets the display name of a faction
     *
     * @param player the player attempting to change the display name
     * @param displayName the new display name
     * @return result containing the status of the operation
     */
    RenameDisplayResult setFactionDisplayName(Player player, String displayName);

    /**
     * Toggles whether the faction is open for anyone to join
     *
     * @param player the player attempting to toggle the open status
     * @return result containing the new open status
     */
    OpenResult toggleFactionOpen(Player player);

    /**
     * Gets the display name of a faction by its UUID
     *
     * @param factionId the UUID of the faction
     * @return the display name or empty string if not found
     */
    String getFactionDisplayName(UUID factionId);

    /**
     * Gets the display name of a player's faction
     *
     * @param target the player whose faction display name to get
     * @return the display name or empty string if not in a faction
     */
    String getFactionDisplayName(Player target);

    // ==============================================
    // PLAYER MANAGEMENT METHODS
    // ==============================================

    /**
     * Gets a FactionPlayer object by UUID
     *
     * @param uuid the UUID of the player
     * @return the FactionPlayer object or null if not found
     */
    FactionPlayer getFactionPlayer(UUID uuid);

    /**
     * Gets a FactionPlayer object by Player object
     *
     * @param player the player
     * @return the FactionPlayer object or null if not found
     */
    FactionPlayer getFactionPlayer(Player player);

    /**
     * Gets a FactionPlayer object by player name
     *
     * @param name the name of the player
     * @return the FactionPlayer object or null if not found
     */
    FactionPlayer getFactionPlayer(String name);

    /**
     * Gets a player's faction by their UUID
     *
     * @param uniqueId the UUID of the player
     * @return the player's faction or null if not in a faction
     */
    Faction getFactionByPlayer(UUID uniqueId);

    /**
     * Invites a player to a faction
     *
     * @param player the player sending the invitation
     * @param toInvite the name of the player to invite
     * @return result containing the invitation status
     */
    InviteResult invitePlayer(Player player, String toInvite);

    /**
     * Revokes an invitation to a player
     *
     * @param player the player revoking the invitation
     * @param toUninvite the name of the player to uninvite
     * @return result containing the uninvite status
     */
    UninviteResult uninvitePlayer(Player player, String toUninvite);

    /**
     * Allows a player to join a faction
     *
     * @param player the player wanting to join
     * @param factionName the name of the faction to join
     * @return result containing the join status
     */
    JoinResult joinFaction(Player player, String factionName);

    /**
     * Kicks a player from a faction
     *
     * @param kicker the player performing the kick (can be null for console)
     * @param playerName the name of the player to kick
     * @return result containing the kick status
     */
    KickResult kickPlayer(Player kicker, String playerName);

    /**
     * Promotes a player to a higher rank
     *
     * @param player the player performing the promotion
     * @param playerName the name of the player to promote
     * @return result containing the promotion status and new rank
     */
    RankChangeResult promotePlayer(Player player, String playerName);

    /**
     * Demotes a player to a lower rank
     *
     * @param player the player performing the demotion
     * @param playerName the name of the player to demote
     * @return result containing the demotion status and new rank
     */
    RankChangeResult demotePlayer(Player player, String playerName);

    /**
     * Gets the rank stars for a player
     *
     * @param uniqueId the UUID of the player
     * @return the rank stars or empty string if not found
     */
    String getRankStars(UUID uniqueId);

    /**
     * Adds a death to a player's statistics
     *
     * @param player the player who died
     * @return result containing the operation status
     */
    AddDeathResult addPlayerDeath(Player player);

    /**
     * Updates a player's power
     *
     * @param player the player whose power to update
     * @param amount the amount to add to power (can be negative)
     * @param publishUpdate whether to publish the update to Redis
     */
    void updatePlayerPower(Player player, double amount, boolean publishUpdate);

    /**
     * Updates a FactionPlayer's power
     *
     * @param player the FactionPlayer whose power to update
     * @param amount the amount to add to power (can be negative)
     * @param publishUpdate whether to publish the update to Redis
     */
    void updatePlayerPower(FactionPlayer player, double amount, boolean publishUpdate);

    // ==============================================
    // CHAT & COMMUNICATION METHODS
    // ==============================================

    /**
     * Toggles a player's chat mode between faction and global chat
     *
     * @param player the player toggling chat mode
     * @return result containing the new chat mode
     */
    ToggleChatResult toggleChatMode(Player player);

    /**
     * Sends a message to the player's faction
     *
     * @param player the player sending the message
     * @param message the message to send
     * @return result containing the send status
     */
    FactionChatResult sendFactionMessage(Player player, String message);

    /**
     * Sends a message to the player's faction and allies
     *
     * @param player the player sending the message
     * @param message the message to send
     * @return result containing the send status
     */
    FactionChatResult sendAllianceMessage(Player player, String message);

    /**
     * Sets the faction's discord link
     *
     * @param player the player setting the discord link
     * @param discordLink the discord invite link
     * @return result containing the operation status
     */
    DiscordResult setFactionDiscord(Player player, String discordLink);

    /**
     * Sets the faction's announcement
     *
     * @param player the player setting the announcement
     * @param announcement the announcement text
     * @return result containing the operation status
     */
    AnnouncementResult setFactionAnnouncement(Player player, String announcement);

    // ==============================================
    // FACTION RELATIONS METHODS
    // ==============================================

    /**
     * Sets the relation between two factions
     *
     * @param player the player setting the relation
     * @param otherFactionName the name of the other faction
     * @param relationName the relation type (ALLY, NEUTRAL, ENEMY)
     * @return result containing the relation setting status
     */
    SetRelationResult setFactionRelation(Player player, String otherFactionName, String relationName);

    /**
     * Gets the relation between a player's faction and another faction
     *
     * @param player the player checking the relation
     * @param otherFactionName the name of the other faction
     * @return the relation type between the factions
     */
    RelationType getFactionRelation(Player player, String otherFactionName);

    /**
     * Gets the relation color between two players
     *
     * @param viewer the viewer player
     * @param target the target player
     * @return the color code representing their factions' relation
     */
    String getRelationColor(Player viewer, Player target);

    // ==============================================
    // FACTION CHEST METHODS
    // ==============================================

    /**
     * Opens the faction chest for a player
     *
     * @param player the player opening the chest
     * @return result containing the operation status
     */
    OpenChestResult openFactionChest(Player player);

    /**
     * Starts a chest update operation (locks chest for editing)
     *
     * @param faction the faction whose chest to update
     * @return true if the update can proceed, false if already being edited
     */
    boolean startChestUpdate(Faction faction);

    /**
     * Ends a chest update operation
     *
     * @param faction the faction whose chest was updated
     * @param updateChestContent whether to save the chest contents to database
     */
    void endChestUpdate(Faction faction, boolean updateChestContent);

    // ==============================================
    // FACTION HOME & RALLY METHODS
    // ==============================================

    /**
     * Sets the faction home location
     *
     * @param player the player setting the home
     * @param homeLocation the location to set as home
     * @return result containing the operation status
     */
    SetHomeResult setFactionHome(Player player, Location homeLocation);

    /**
     * Gets the faction home location
     *
     * @param player the player requesting the home
     * @return result containing the home location if available
     */
    HomeResult getFactionHome(Player player);

    /**
     * Sets a rally point for the faction
     *
     * @param player the player setting the rally point
     * @return result containing the operation status
     */
    RallyResult setFactionRally(Player player);

    // ==============================================
    // FACTION FOCUS METHODS
    // ==============================================

    /**
     * Sets a faction as the focused faction
     *
     * @param player the player setting the focus
     * @param factionName the name of the faction to focus on
     * @return result containing the operation status
     */
    FocusResult setFocusedFaction(Player player, String factionName);

    /**
     * Checks if a faction is focused by the viewer's faction
     *
     * @param viewer the viewer player
     * @param target the target player (to check their faction)
     * @return true if the target's faction is focused by the viewer's faction
     */
    boolean isFocusedFaction(Player viewer, Player target);

    // ==============================================
    // ECONOMY METHODS
    // ==============================================

    /**
     * Withdraws money from the faction bank
     *
     * @param player the player withdrawing money
     * @param amount the amount to withdraw
     * @return result containing the withdrawal status and amount
     */
    WithdrawResult withdrawFromFaction(Player player, double amount);

    /**
     * Deposits money to the faction bank
     *
     * @param player the player depositing money
     * @param amount the amount to deposit
     * @return result containing the deposit status and amount
     */
    DepositResult depositToFaction(Player player, double amount);

    // ==============================================
    // COMBAT & STATISTICS METHODS
    // ==============================================

    /**
     * Adds a kill to a player's and faction's statistics
     *
     * @param player the player who got the kill
     * @param killed the player who was killed
     * @return result containing the operation status
     */
    AddKillResult addPlayerKill(Player player, Player killed);

    /**
     * Gets a player's kill count
     *
     * @param player the player to check
     * @return the number of kills
     */
    int getPlayerKills(Player player);

    /**
     * Adds an event win to a faction
     *
     * @param player the player whose faction won an event
     * @return result containing the operation status
     */
    AddEventsWonResult addFactionEventWin(Player player);

    /**
     * Gets a faction's event win count
     *
     * @param player the player whose faction to check
     * @return the number of event wins
     */
    int getFactionEventWins(Player player);

    /**
     * Toggles friendly fire for a faction
     *
     * @param player the player toggling friendly fire
     * @return result containing the new friendly fire status
     */
    FriendlyFireResult toggleFriendlyFire(Player player);

    // ==============================================
    // CLAIM MANAGEMENT METHODS
    // ==============================================

    /**
     * Claims a chunk for a faction
     *
     * @param claimingFaction the UUID of the claiming faction
     * @param x the x coordinate of the chunk
     * @param z the z coordinate of the chunk
     * @param worldName the name of the world
     * @param publishUpdate whether to publish the claim to Redis
     * @return result containing the claim status
     */
    ClaimResult claimChunk(UUID claimingFaction, int x, int z, String worldName, boolean publishUpdate);

    /**
     * Checks if a chunk can be claimed by a faction
     *
     * @param claimingFactionId the UUID of the claiming faction
     * @param x the x coordinate of the chunk
     * @param z the z coordinate of the chunk
     * @param worldName the name of the world
     * @return result indicating whether the chunk can be claimed
     */
    ClaimResult canClaimChunk(UUID claimingFactionId, int x, int z, String worldName);

    /**
     * Gets the faction that owns a block
     *
     * @param block the block to check
     * @return the faction that owns the chunk or null if unclaimed
     */
    Faction getFactionAtBlock(org.bukkit.block.Block block);

    /**
     * Checks if two players are in the same team (faction)
     *
     * @param player the first player
     * @param toFactionId the UUID of the second player's faction
     * @return true if both players are in the same faction
     */
    boolean isSameTeam(Player player, UUID toFactionId);

    // ==============================================
    // EVENT METHODS
    // ==============================================

    /**
     * Gets the current clan event
     *
     * @return the current clan event or null if no event is running
     */
    ClanEvent getCurrentEvent();
}