package com.wallet.crypto.alphawallet.di;

import com.wallet.crypto.alphawallet.interact.AddTokenInteract;
import com.wallet.crypto.alphawallet.interact.FetchTokensInteract;
import com.wallet.crypto.alphawallet.interact.FetchTransactionsInteract;
import com.wallet.crypto.alphawallet.interact.FindDefaultNetworkInteract;
import com.wallet.crypto.alphawallet.interact.FindDefaultWalletInteract;
import com.wallet.crypto.alphawallet.interact.GetDefaultWalletBalance;
import com.wallet.crypto.alphawallet.interact.SetupTokensInteract;
import com.wallet.crypto.alphawallet.repository.EthereumNetworkRepositoryType;
import com.wallet.crypto.alphawallet.repository.TokenRepositoryType;
import com.wallet.crypto.alphawallet.repository.TransactionRepositoryType;
import com.wallet.crypto.alphawallet.repository.WalletRepositoryType;
import com.wallet.crypto.alphawallet.router.ExternalBrowserRouter;
import com.wallet.crypto.alphawallet.router.HomeRouter;
import com.wallet.crypto.alphawallet.router.MarketBrowseRouter;
import com.wallet.crypto.alphawallet.router.MarketplaceRouter;
import com.wallet.crypto.alphawallet.router.MyTokensRouter;
import com.wallet.crypto.alphawallet.router.NewSettingsRouter;
import com.wallet.crypto.alphawallet.router.SettingsRouter;
import com.wallet.crypto.alphawallet.router.TransactionDetailRouter;
import com.wallet.crypto.alphawallet.router.WalletRouter;
import com.wallet.crypto.alphawallet.viewmodel.TransactionsViewModelFactory;

import dagger.Module;
import dagger.Provides;

@Module
class TransactionsModule {
    @Provides
    TransactionsViewModelFactory provideTransactionsViewModelFactory(
            FindDefaultNetworkInteract findDefaultNetworkInteract,
            FindDefaultWalletInteract findDefaultWalletInteract,
            FetchTransactionsInteract fetchTransactionsInteract,
            FetchTokensInteract fetchTokensInteract,
            GetDefaultWalletBalance getDefaultWalletBalance,
            SetupTokensInteract setupTokensInteract,
            SettingsRouter settingsRouter,
            AddTokenInteract addTokenInteract,
            TransactionDetailRouter transactionDetailRouter,
            MyTokensRouter myTokensRouter,
            ExternalBrowserRouter externalBrowserRouter,
            MarketBrowseRouter marketBrowseRouter,
            WalletRouter walletRouter,
            MarketplaceRouter marketplaceRouter,
            NewSettingsRouter newSettingsRouter,
            HomeRouter homeRouter) {
        return new TransactionsViewModelFactory(
                findDefaultNetworkInteract,
                findDefaultWalletInteract,
                fetchTransactionsInteract,
                fetchTokensInteract,
                getDefaultWalletBalance,
                setupTokensInteract,
                settingsRouter,
                addTokenInteract,
                transactionDetailRouter,
                myTokensRouter,
                externalBrowserRouter,
                marketBrowseRouter,
                walletRouter,
                marketplaceRouter,
                newSettingsRouter,
                homeRouter);
    }

    @Provides
    FindDefaultNetworkInteract provideFindDefaultNetworkInteract(
            EthereumNetworkRepositoryType ethereumNetworkRepositoryType) {
        return new FindDefaultNetworkInteract(ethereumNetworkRepositoryType);
    }

    @Provides
    FindDefaultWalletInteract provideFindDefaultWalletInteract(WalletRepositoryType walletRepository) {
        return new FindDefaultWalletInteract(walletRepository);
    }

    @Provides
    FetchTransactionsInteract provideFetchTransactionsInteract(TransactionRepositoryType transactionRepository) {
        return new FetchTransactionsInteract(transactionRepository);
    }

    @Provides
    FetchTokensInteract provideFetchTokensInteract(TokenRepositoryType tokenRepository) {
        return new FetchTokensInteract(tokenRepository);
    }

    @Provides
    GetDefaultWalletBalance provideGetDefaultWalletBalance(
            WalletRepositoryType walletRepository, EthereumNetworkRepositoryType ethereumNetworkRepository) {
        return new GetDefaultWalletBalance(walletRepository, ethereumNetworkRepository);
    }

    @Provides
    SettingsRouter provideSettingsRouter() {
        return new SettingsRouter();
    }

    @Provides
    TransactionDetailRouter provideTransactionDetailRouter() {
        return new TransactionDetailRouter();
    }

    @Provides
    MyTokensRouter provideMyTokensRouter() {
        return new MyTokensRouter();
    }

    @Provides
    ExternalBrowserRouter provideExternalBrowserRouter() {
        return new ExternalBrowserRouter();
    }

    @Provides
    MarketBrowseRouter provideMarketBrowseRouter() { return new MarketBrowseRouter(); }

    @Provides
    WalletRouter providesWalletRouter() { return new WalletRouter(); }

    @Provides
    MarketplaceRouter providesMarketplaceRouter() { return new MarketplaceRouter(); }

    @Provides
    NewSettingsRouter providesNewSettingsRouter() { return new NewSettingsRouter(); }

    @Provides
    HomeRouter providesHomeRouter() { return new HomeRouter(); }

    @Provides
    AddTokenInteract provideAddTokenInteract(
            TokenRepositoryType tokenRepository,
            WalletRepositoryType walletRepository) {
        return new AddTokenInteract(walletRepository, tokenRepository);
    }

    @Provides
    SetupTokensInteract provideSetupTokensInteract(TokenRepositoryType tokenRepository) {
        return new SetupTokensInteract(tokenRepository);
    }
}
