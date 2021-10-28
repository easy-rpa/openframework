package eu.ibagroup.easyrpa.openframework.email.service.ews;

import eu.ibagroup.easyrpa.openframework.email.exception.EmailMessagingException;
import eu.ibagroup.easyrpa.openframework.email.message.EmailMessage;
import eu.ibagroup.easyrpa.openframework.email.service.EmailClient;
import eu.ibagroup.easyrpa.openframework.email.service.MessageConverter;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.FolderTraversal;
import microsoft.exchange.webservices.data.core.enumeration.search.ItemTraversal;
import microsoft.exchange.webservices.data.core.enumeration.search.LogicalOperator;
import microsoft.exchange.webservices.data.core.enumeration.search.SortDirection;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.definition.PropertyDefinitionBase;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import org.apache.commons.lang3.NotImplementedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EwsEmailsClient implements EmailClient {
    private ExchangeService service;

    private String host;

    private String domain;

    private WebCredentials credentials;

    private MessageConverter<Item> messageConverter;

    private ExchangeVersion exchangeVersion;

    private PropertySet bindPropSet = new PropertySet();

    public EwsEmailsClient(String host, String username, String password, String domain, ExchangeVersion exchangeVersion) {
        this.host = host;
        this.exchangeVersion = exchangeVersion;
        this.credentials = new WebCredentials(username, password, domain);
        initEwsEmailClient(this.host, this.credentials, exchangeVersion);
    }

    public EwsEmailsClient(String host, String username, String password, ExchangeVersion exchangeVersion) {
        this.host = host;
        this.exchangeVersion = exchangeVersion;
        this.credentials = new WebCredentials(username, password);
        initEwsEmailClient(this.host, this.credentials, exchangeVersion);
    }

    public EwsEmailsClient(String host, String username, String password) {
        this.host = host;
        this.exchangeVersion = ExchangeVersion.Exchange2010_SP2;
        this.credentials = new WebCredentials(username, password);
        initEwsEmailClient(this.host, this.credentials, this.exchangeVersion);
    }

    private void initEwsEmailClient(String host, WebCredentials credentials, ExchangeVersion exchangeVersion) {
        this.service = new ExchangeService(this.exchangeVersion);
        this.messageConverter = new EwsMessageConverter(this.service);
        try {
            service.setUrl(new URI(this.host));
            service.setCredentials(this.credentials);
        } catch (URISyntaxException e) {
            throw new EmailMessagingException(e);
        }
    }

    public boolean isConnected() {
        if (host == null || credentials == null) {
            return false;
        } else {
            try {
                Folder folder;
                folder = Folder.bind(service, WellKnownFolderName.Root);
                return (folder != null);
            } catch (Exception e) {
                throw new EmailMessagingException(e);
            }
        }
    }

    @Override
    public boolean createFolder(String folderName) throws EmailMessagingException {
        createFolder(folderName, WellKnownFolderName.Inbox);
        return true;
    }

    public Folder createFolder(String folderName, WellKnownFolderName wellKnownFolderName) throws EmailMessagingException {
        try {
            Folder newFolder = new Folder(service);
            newFolder.setDisplayName(folderName);
            newFolder.save(wellKnownFolderName);
            return newFolder;
        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    public Folder createFolder(String folderName, FolderId parentFolderId) throws EmailMessagingException {
        try {
            Folder newFolder = new Folder(service);
            newFolder.setDisplayName(folderName);
            newFolder.save(parentFolderId);
            return newFolder;
        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public boolean removeMessage(EmailMessage message) throws EmailMessagingException {
        try {
            microsoft.exchange.webservices.data.core.service.item.EmailMessage email = microsoft.exchange.webservices.data.core.service.item.EmailMessage.bind(service, new ItemId(message.getId().get()));
            email.delete(DeleteMode.HardDelete);
            return true;
        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }

    }

    @Override
    public EmailMessage putMessage(EmailMessage message, String targetFolder) throws EmailMessagingException {
        throw new NotImplementedException("Method putMessage is not implemented");
    }

    @Override
    public EmailMessage copyMessage(EmailMessage message, String targetFolder) throws EmailMessagingException {
        try {
            microsoft.exchange.webservices.data.core.service.item.EmailMessage email = microsoft.exchange.webservices.data.core.service.item.EmailMessage.bind(service, new ItemId(message.getId().isPresent() ? message.getId().get() : null));
            Folder folder = getFolder(targetFolder);
            email.copy(folder.getId());
            return messageConverter.convertToEmailMessage(email);

        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    public EmailMessage moveMessage(String emailId, String targetFolder) throws EmailMessagingException {
        try {
            microsoft.exchange.webservices.data.core.service.item.EmailMessage email = microsoft.exchange.webservices.data.core.service.item.EmailMessage.bind(service, new ItemId(emailId));
            Folder folder = getFolder(targetFolder);
            email.move(folder.getId());
            return messageConverter.convertToEmailMessage(email);

        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public EmailMessage moveMessage(EmailMessage message, String targetFolder) throws EmailMessagingException {
        return moveMessage(message.getId().orElse(null), targetFolder);
    }

    @Override
    public List<EmailMessage> fetchMessages(String folderName, Predicate<EmailMessage> isSatisfy) throws EmailMessagingException {
        //TODO Implement this
        return null;
    }

    @Override
    public List<EmailMessage> fetchAllMessages(String folderName) throws EmailMessagingException {

        if (this.bindPropSet.getCount() == 0) {
            this.bindPropSet = new PropertySet(BasePropertySet.FirstClassProperties);
            this.bindPropSet.setRequestedBodyType(BodyType.HTML);
        }
        return fetchMessages(folderName, this.bindPropSet, null, false);
    }

    public List<EmailMessage> fetchMessages(String folderName, PropertySet bindPropSet, SearchFilter searchFilter, boolean markAsRead) throws EmailMessagingException {
        try {
            ItemView itemView = new ItemView(Integer.MAX_VALUE);
            itemView.getOrderBy().add(ItemSchema.DateTimeReceived, SortDirection.Ascending);

            Folder folder = getFolder(folderName);

            FindItemsResults<Item> itemsResults;
            if (searchFilter == null) {
                itemsResults = service.findItems(folder.getId(), itemView);
            } else {
                itemsResults = service.findItems(folder.getId(), searchFilter, itemView);
            }
            List<Item> items = itemsResults.getItems();
            return items.stream().map(i -> {
                try {
                    if (markAsRead) {
                        microsoft.exchange.webservices.data.core.service.item.EmailMessage email = (microsoft.exchange.webservices.data.core.service.item.EmailMessage) i;
                        email.setIsRead(true);
                        email.update(ConflictResolutionMode.AlwaysOverwrite);
                    }
                    i.load(bindPropSet);
                } catch (Exception e) {
                }
                return this.messageConverter.convertToEmailMessage(i);
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public List<EmailMessage> fetchUnreadMessages(String folderName, boolean markRead) throws EmailMessagingException {
        if (this.bindPropSet.getCount() == 0) {
            this.bindPropSet = new PropertySet(BasePropertySet.FirstClassProperties);
            this.bindPropSet.setRequestedBodyType(BodyType.HTML);
        }
        SearchFilter sf = new SearchFilter.SearchFilterCollection(LogicalOperator.And, new SearchFilter.IsEqualTo(EmailMessageSchema.IsRead, false));
        return fetchMessages(folderName, this.bindPropSet, sf, markRead);
    }

    @Override
    public List<String> fetchFolderList() throws EmailMessagingException {
        FolderView view = new FolderView(Integer.MAX_VALUE);
        view.setTraversal(FolderTraversal.Deep);
        FindFoldersResults findFoldersResults;
        try {
            findFoldersResults = service.findFolders(WellKnownFolderName.Root, view);
            return findFoldersResults.getFolders().stream().map(f -> {
                try {
                    return f.getDisplayName();
                } catch (ServiceLocalException e) {
                    return null;
                }
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public String getDefaultInboxFolder() throws EmailMessagingException {
        try {
            return getInboxFolder().getDisplayName();
        } catch (ServiceLocalException e) {
            throw new EmailMessagingException(e);
        }
    }

    @Override
    public void setDefaultInboxFolder(String defaultInboxFolder) throws EmailMessagingException {
        throw new UnsupportedOperationException("Not supported for Exchange service");
    }

    public Folder getInboxFolder() throws EmailMessagingException {
        try {
            return Folder.bind(this.service, WellKnownFolderName.Inbox);
        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    public Folder getFolder(String folderName) throws EmailMessagingException {
        return getFolder(folderName, false, null);
    }

    public Folder getFolder(String folderName, boolean createIfNotExist, FolderId parentFolderId) throws EmailMessagingException {
        SearchFilter searchFilterByName = new SearchFilter.IsEqualTo(FolderSchema.DisplayName, folderName);
        FolderView view = new FolderView(Integer.MAX_VALUE);
        view.setTraversal(FolderTraversal.Deep);
        FindFoldersResults findFoldersResults;
        try {
            findFoldersResults = service.findFolders(WellKnownFolderName.Root, searchFilterByName, view);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailMessagingException("Couldn't find folder " + folderName);
        }
        List<Folder> folderList = findFoldersResults.getFolders();

        if (!folderList.isEmpty()) {
            return folderList.get(0);
        } else {
            if (createIfNotExist && parentFolderId != null) {
                return createFolder(folderName, parentFolderId);
            } else {
                throw new EmailMessagingException("Couldn't find folder " + folderName);
            }

        }
    }

    public ExchangeService getService() {
        return service;
    }

    public void setService(ExchangeService service) {
        this.service = service;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public ExchangeVersion getExchangeVersion() {
        return exchangeVersion;
    }

    public void setExchangeVersion(ExchangeVersion exchangeVersion) {
        this.exchangeVersion = exchangeVersion;
    }

    public void setCredentials(WebCredentials credentials) {
        this.credentials = credentials;
    }

    public PropertySet getEmailPropertySet() {
        return bindPropSet;
    }

    public void setEmailPropertySet(PropertySet bindPropSet) {
        this.bindPropSet = bindPropSet;
    }

    public void addEmailPropertySet(PropertyDefinitionBase property) throws Exception {
        this.bindPropSet.add(property);
    }

    public Folder getFolderById(FolderId id) {
        try {
            return Folder.bind(service, id);
        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    public EmailMessage getByEmailId(ItemId itemId) {
        try {
            microsoft.exchange.webservices.data.core.service.item.EmailMessage emailMessage = microsoft.exchange.webservices.data.core.service.item.EmailMessage.bind(service, itemId);
            return messageConverter.convertToEmailMessage(emailMessage);
        } catch (Exception e) {
            throw new EmailMessagingException(e);
        }
    }

    public List<EmailMessage> searchEmailWithFilter(String folderName, SearchFilter filter) {
        ItemView itemView = new ItemView(Integer.MAX_VALUE);
        itemView.setTraversal(ItemTraversal.Shallow);
        itemView.setPropertySet(new PropertySet(BasePropertySet.FirstClassProperties));
        try {
            itemView.getOrderBy().add(ItemSchema.DateTimeReceived, SortDirection.Descending);
        } catch (ServiceLocalException e) {
            return null;
        }
        FindItemsResults<Item> findItemsResults = null;
        Folder folder = getFolder(folderName);
        if (folder == null) {
            throw new RuntimeException("Could not find folder");
        }

        try {
            findItemsResults = service.findItems(folder.getId(), filter, itemView);
        } catch (Exception e) {
            return null;
        }

        return findItemsResults.getItems().stream().map(e -> this.messageConverter.convertToEmailMessage(e)).collect(Collectors.toList());
    }
}
